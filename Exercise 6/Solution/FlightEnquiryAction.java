package com.flight;

import com.intuit.benten.common.actionhandlers.BentenActionHandler;
import com.intuit.benten.common.actionhandlers.BentenHandlerResponse;
import com.intuit.benten.common.actionhandlers.BentenSlackResponse;
import com.intuit.benten.common.annotations.ActionHandler;
import com.intuit.benten.common.formatters.SlackFormatter;
import com.intuit.benten.common.helpers.BentenMessageHelper;
import com.intuit.benten.common.http.HttpHelper;
import com.intuit.benten.common.nlp.BentenMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.json.*;

import javafx.util.*;
import java.util.*;



@Component
@ActionHandler(action = "action_flight_enquiry")
public class FlightEnquiryAction implements BentenActionHandler {

    @Autowired
    HttpHelper httpHelper;

    @Override
    public BentenHandlerResponse handle(BentenMessage bentenMessage) {

        //get data from dialog flow - entered by user in slcack conversartion
        String originPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "from");
        String destinationPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "to");
        String dateOfTravel = BentenMessageHelper.getParameterAsString(bentenMessage, "date");

        //Create bentenhandler response and slack response object
        BentenHandlerResponse bentenHandlerResponse = new BentenHandlerResponse();
        BentenSlackResponse bentenSlackResponse = new BentenSlackResponse();
        bentenHandlerResponse.setBentenSlackResponse(bentenSlackResponse);

        try {
            String url = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browseroutes/v1.0/IN/INR/en-US";
            url = url + "/"+originPlace+"/"+destinationPlace+"/"+dateOfTravel;
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("X-RapidAPI-Key","54fb4ccfa3mshf559f38b0b01efcp12d9ddjsne5ad978facb6");
            HttpResponse httpResponse = httpHelper.getClient().execute(httpGet);



            String response = EntityUtils.toString(httpResponse.getEntity());



            if(httpResponse.getStatusLine().getStatusCode() >= 400){
                response = "INVALID REQUEST";
            }


            Thread.sleep(1000);



            JSONObject jsonObject = new JSONObject(response);

            org.json.JSONArray quotes = jsonObject.getJSONArray("Quotes");
            org.json.JSONArray carriers = jsonObject.getJSONArray("Carriers");

            Map<String, ArrayList<Pair<Double,String>>> flightMap = new HashMap<>();


            for(int i=0;i<quotes.length();i++){

                String priceString = quotes.getJSONObject(i).get("MinPrice").toString();

                String time = quotes.getJSONObject(i).get("QuoteDateTime").toString();
                time = time.substring(time.indexOf("T")+1,time.length()-1);

                JSONObject OutboundLeg = quotes.getJSONObject(i).getJSONObject("OutboundLeg");
                String carrierCode = OutboundLeg.getJSONArray("CarrierIds").get(0).toString();

                //getting the carrier name maaped to the carrier code
                String carrier = getCarrierNameFromCode(carriers,carrierCode);


                Double price = Double.parseDouble(priceString);


                //adding the data to a map with carrier as a key

                if( flightMap.get(carrier) == null){
                    ArrayList<Pair<Double, String> > arrayList = new ArrayList<Pair <Double, String> >();
                    arrayList.add(new Pair<Double, String>(price, time));
                    flightMap.put(carrier, arrayList);
                }else {
                    ArrayList<Pair<Double, String> >  arrayList = flightMap.get(carrier);
                    arrayList.add(new Pair <Double, String>( price,time));
                    flightMap.put(carrier, arrayList);
                }
            }



            //creating response message

            Iterator it = flightMap.entrySet().iterator();
            String responseMessage = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                responseMessage =responseMessage+"\n"+ pair.getKey().toString()+"\n";
                ArrayList<Pair<Double,String>> arrayList = (ArrayList<Pair<Double,String>>)pair.getValue();

                arrayList.sort(Comparator.comparing(Pair::getKey));

                for(int i=0;i<arrayList.size();i++)
                    responseMessage = responseMessage + "Price:"+arrayList.get(i).getKey().toString()+" || Time: "+arrayList.get(i).getValue() + "\n";
            }
            bentenSlackResponse.setSlackText(responseMessage);
            bentenMessage.getChannel().sendMessage(bentenHandlerResponse,bentenMessage.getChannelInformation());



        } catch (Exception ex) {
            // not handled
        }

        bentenSlackResponse.setSlackText("`Ok I completed what you asked me to do`.");

        return bentenHandlerResponse;

    }


    //function to get carrier name given carrier id - from the Carriers json
    String getCarrierNameFromCode(org.json.JSONArray carriers, String code) throws Exception{
        String carrier=null;
        for(int i=0;i<carriers.length();i++){
            System.out.println();
            if(carriers.getJSONObject(i).get("CarrierId").toString().equals(code)){
                return carriers.getJSONObject(i).get("Name").toString();
            }
        }
        return carrier;
    }
}
