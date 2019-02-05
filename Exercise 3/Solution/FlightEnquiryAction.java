package com.flight;

import com.intuit.benten.common.actionhandlers.BentenActionHandler;
import com.intuit.benten.common.actionhandlers.BentenHandlerResponse;
import com.intuit.benten.common.actionhandlers.BentenSlackResponse;
import com.intuit.benten.common.annotations.ActionHandler;
import com.intuit.benten.common.formatters.SlackFormatter;
import com.intuit.benten.common.helpers.BentenMessageHelper;
import com.intuit.benten.common.nlp.BentenMessage;
import org.springframework.stereotype.Component;



@Component
@ActionHandler(action = "action_flight_enquiry")
public class FlightEnquiryAction implements BentenActionHandler{

  @Override
  public BentenHandlerResponse handle(BentenMessage bentenMessage) {

          String originPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "from");
          String destinationPlace = BentenMessageHelper.getParameterAsString(bentenMessage, "to");
          String dateOfTravel = BentenMessageHelper.getParameterAsString(bentenMessage, "date");

          BentenHandlerResponse bentenHandlerResponse = new BentenHandlerResponse();
          BentenSlackResponse bentenSlackResponse = new BentenSlackResponse();
          bentenHandlerResponse.setBentenSlackResponse(bentenSlackResponse);
          try {

              Thread.sleep(1000);

              String response = "Thanks for your input. Checking the flight details from " + originPlace + " to " + destinationPlace + " on " + dateOfTravel;
              response = SlackFormatter.create().preformatted(response).build();
              bentenSlackResponse.setSlackText(response);
          }
          catch (Exception ex) {
              // not handled
          }

          return bentenHandlerResponse;



  }
}
