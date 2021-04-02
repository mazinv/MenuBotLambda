package cz.zcu.fav.kiv.eitm.menubot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class MenuBotLambda implements RequestHandler<Map<String, Object>, Object> {
    private cz.zcu.fav.kiv.eitm.menubot.RequestHandler requestHandler;

    public MenuBotLambda() {
        requestHandler = new cz.zcu.fav.kiv.eitm.menubot.RequestHandler();
    }

    @Override
    public Object handleRequest(Map<String, Object> input, Context context) {
        return requestHandler.handleRequestSimpleResponse(input);
    }
}
