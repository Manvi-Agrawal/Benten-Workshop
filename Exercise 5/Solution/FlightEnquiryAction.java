package com.flight;

import com.intuit.benten.common.actionhandlers.BentenActionHandler;
import com.intuit.benten.common.actionhandlers.BentenHandlerResponse;
import com.intuit.benten.common.actionhandlers.BentenSlackResponse;
import com.intuit.benten.common.annotations.ActionHandler;
import com.intuit.benten.common.formatters.SlackFormatter;
import com.intuit.benten.common.helpers.BentenMessageHelper;
import com.intuit.benten.common.nlp.BentenMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.intuit.benten.common.http.HttpHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;



@Component
@ActionHandler(action = "action_flight_enquiry")
public class FlightEnquiryAction implements BentenActionHandler{

    @Autowired
    HttpHelper httpHelper;

  @Override
  public BentenHandlerResponse handle(BentenMessage bentenMessage) {

          String originPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "from");
          String destinationPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "to");
          String dateOfTravel = BentenMessageHelper.getParameterAsString(bentenMessage, "date");

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

              String responseToSlack = "Thanks for your input. Checking the flight details from " + originPlace + " to " + destinationPlace + " on " + dateOfTravel;
              responseToSlack = SlackFormatter.create().preformatted(responseToSlack).build();
              bentenSlackResponse.setSlackText(responseToSlack);
              bentenMessage.getChannel().sendMessage(bentenHandlerResponse,bentenMessage.getChannelInformation());


              bentenSlackResponse.setSlackText(response);

          }
          catch (Exception ex) {
              // not handled
          }


          return bentenHandlerResponse;

 }
}
