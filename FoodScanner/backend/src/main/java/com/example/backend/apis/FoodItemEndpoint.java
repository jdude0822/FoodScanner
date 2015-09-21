package com.example.backend.apis;

/**
 * Created by mlenarto on 9/17/15.
 */

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.example.backend.Constants;
import static com.example.backend.OfyService.ofy;
import com.example.backend.model.FoodItem;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "foodScannerBackendAPI",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE},
        namespace = @ApiNamespace(
                ownerDomain = "backend.example.com",
                ownerName = "backend.example.com",
                packagePath = ""
        )
)

public class FoodItemEndpoint {

    /**
     * Endpoint method that takes in a name, queries the database for that name,
     * and returns a FoodItem from the query.
     */
    @ApiMethod(name = "getDensity")
    public FoodItem getFoodItem(@Named("name") String name) {
        return ofy().load().type(FoodItem.class).filter("name", name).list().get(0);
    }

    @ApiMethod(httpMethod = "GET")
    public List<FoodItem> getFoodItems() {

        Query<FoodItem> query = ofy().load().type(FoodItem.class);
        List<FoodItem> results = new ArrayList<FoodItem>();
        QueryResultIterator<FoodItem> iterator = query.iterator();

        while (iterator.hasNext()) {
            results.add(iterator.next());
        }

        return results;
    }

}