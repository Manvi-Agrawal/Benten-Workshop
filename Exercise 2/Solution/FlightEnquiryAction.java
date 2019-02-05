package com.flight;

import com.intuit.benten.common.actionhandlers.BentenActionHandler;
import com.intuit.benten.common.actionhandlers.BentenHandlerResponse;
import com.intuit.benten.common.annotations.ActionHandler;
import com.intuit.benten.common.helpers.BentenMessageHelper;
import com.intuit.benten.common.http.HttpHelper;
import com.intuit.benten.common.nlp.BentenMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




@Component
@ActionHandler(action = "action_flight_enquiry")
public class FlightEnquiryAction implements BentenActionHandler {

  @Override
  public BentenHandlerResponse handle(BentenMessage bentenMessage) {
    String originPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "from");
    String destinationPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "to");
    String dateOfTravel = BentenMessageHelper.getParameterAsString(bentenMessage, "date");

    BentenHandlerResponse bentenHandlerResponse = new BentenHandlerResponse();



      System.out.println("Origin: "+originPlace);
      System.out.println("Destination: "+ destinationPlace);
      System.out.println("Date: "+dateOfTravel);

   return bentenHandlerResponse;

  }
}
