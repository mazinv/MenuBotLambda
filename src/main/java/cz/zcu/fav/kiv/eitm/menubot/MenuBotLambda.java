package cz.zcu.fav.kiv.eitm.menubot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class MenuBotLambda implements RequestHandler<Map<String, Object>, Object> {
    private cz.zcu.fav.kiv.eitm.menubot.RequestHandler requestHandler;

    public MenuBotLambda() {
        requestHandler = new cz.zcu.fav.kiv.eitm.menubot.RequestHandler();
    }

    @Override
    public Object handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            return requestHandler.handleRequestSimpleResponse(input, logger);
        } catch (Exception e) {
            logger.log(e.getMessage());
        }
        return new Object();
    }
}
