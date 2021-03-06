package com.example.backend;

import com.example.backend.model.DensityEntry;
import com.example.backend.model.BackendFoodItem;
import com.example.backend.model.BackendMeal;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Created by mlenarto on 9/21/15.
 */

public final class OfyService {

    private OfyService() {
    }
    
    static {
        factory().register(BackendFoodItem.class);
        factory().register(DensityEntry.class);
        factory().register(BackendMeal.class);
    }

    /**
     * Returns the Objectify service wrapper.
     * @return The Objectify service wrapper.
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    /**
     * Returns the Objectify factory service.
     * @return The factory service.
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
