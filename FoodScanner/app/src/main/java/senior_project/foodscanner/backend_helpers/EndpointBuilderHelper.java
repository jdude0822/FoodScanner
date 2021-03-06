package senior_project.foodscanner.backend_helpers;

import com.example.backend.foodScannerBackendAPI.FoodScannerBackendAPI;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by mlenarto on 9/24/15.
 */
public final class EndpointBuilderHelper {
    private static final String serverURL = "https://foodscannerwebapp.appspot.com/_ah/api/";
    private static final String localHost = "http://localhost:8080/";

    /**
     * Default constructor, never called.
     */
    private EndpointBuilderHelper() {
    }

    /**
     * @return FoodScannerBackendAPI endpoints to the GAE backend.
     */
    public static FoodScannerBackendAPI getEndpoints(GoogleAccountCredential credential) {

        FoodScannerBackendAPI.Builder builder = new FoodScannerBackendAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), credential)
                .setRootUrl(serverURL);

        return builder.build();
    }
}
