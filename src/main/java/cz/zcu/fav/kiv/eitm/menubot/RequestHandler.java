package cz.zcu.fav.kiv.eitm.menubot;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.List;
import java.util.Map;

public class RequestHandler {
    private QueryHandler queryHandler;

    public RequestHandler() {
        try {
            queryHandler = new QueryHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object handleRequestSimpleResponse(Map<String, Object> input, LambdaLogger logger) {
        String intentName = getIntentName(input);
        logger.log("Handling: " + intentName + " intent");

        switch (intentName) {
            case "IWouldLike":
                return handleIWouldLike(input, logger);
            case "SelectRestaurant":
                return handleSelectRestaurant(input, logger);
        }

        return null;
    }

    private String getIntentName(Map<String, Object> input) {
        return (String) ((Map<String, Object>)input.get("currentIntent")).get("name");
    }

    private LexResponse handleIWouldLike(Map<String, Object> input, LambdaLogger logger) {
        var intent = (Map<String, Object>)input.get("currentIntent");
        var slots = (Map<String, Object>)intent.get("slots");
        var food = (String)slots.get("food");
        logger.log("Searching for food: " + food);

        List<Restaurant> restaurants = queryHandler.queryFood(food);
        String content;
        if (restaurants.size() == 0) {
            content = "We could not find any restaurants with food you would like.";
            Message message = new Message("PlainText", content);
            return new LexResponse(new DialogAction("Close", "Failed", message));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("I can offer you these restaurants: ");
            for (int i = 0; i < restaurants.size(); i++) {
                sb.append(restaurants.get(i).name);
                if (i != restaurants.size() - 1)
                    sb.append(", ");
            }
            content = sb.toString();
            Message message = new Message("PlainText", content);
            return new LexResponse(new DialogAction("Close", "Fulfilled", message));
        }
    }

    private LexResponse handleSelectRestaurant(Map<String, Object> input, LambdaLogger logger) {
        var intent = (Map<String, Object>)input.get("currentIntent");
        var slots = (Map<String, Object>)intent.get("slots");
        var restaurantName = (String)slots.get("restaurant");

        logger.log("Searching for restaurant: " + restaurantName);

        Restaurant restaurant = queryHandler.queryRestaurant(restaurantName);
        String content;
        if (restaurant == null) {
            content = "We could not find any restaurants with given name - " + restaurantName + ".";
            Message message = new Message("PlainText", content);
            return new LexResponse(new DialogAction("Close", "Failed", message));
        } else {
            content = restaurant.name + ": " + restaurant.menuString;
            Message message = new Message("PlainText", content);
            return new LexResponse(new DialogAction("Close", "Fulfilled", message));
        }
    }
}
