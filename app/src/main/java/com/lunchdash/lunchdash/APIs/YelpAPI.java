package com.lunchdash.lunchdash.APIs;

import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class YelpAPI {

    private static final String API_HOST = "api.yelp.com";
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    OAuthService service;
    Token accessToken;

    public YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
                        .apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    public String searchForRestaurants(String term, String latitude, String longitude, String sortBy, String maxDistance) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);

        String ll = latitude + "," + longitude;
        request.addQuerystringParameter("ll", ll); //Specify location by "Geographic Coordinate" aka latitude longitude

        String sortParam;
        switch (sortBy) {
            case "Best Match":
                sortParam = "0";
                break;
            case "Distance":
                sortParam = "1";
                break;
            case "Rating":
                sortParam = "2";
                break;
            default:
                sortParam = "0";
        }
        request.addQuerystringParameter("sort", sortParam);

        String maxDistanceParam;
        switch (maxDistance) {
            case "2 blocks": // 1 block is approximated to about 200 meters
                maxDistanceParam = "400";
                break;
            case "6 blocks":
                maxDistanceParam = "1200";
                break;
            case "1 mile":
                maxDistanceParam = "1609";
                break;
            case "5 miles":
                maxDistanceParam = "8046";
                break;
            default:
                maxDistanceParam = "40000"; // ~24.8548 miles
        }
        request.addQuerystringParameter("radius_filter", maxDistanceParam);

        Log.d("DEBUG", "Querying... term=" + term + " latitude=" + latitude + " longitude=" + longitude + " sortBy=" + sortParam + " maxDistance=" + maxDistanceParam);

        return sendRequestAndGetResponse(request);
    }

    //Business API
    public String searchByBusinessId(String businessID) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        return sendRequestAndGetResponse(request);
    }

    private OAuthRequest createOAuthRequest(String path) {
        return new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
    }

    private String sendRequestAndGetResponse(OAuthRequest request) {
        System.out.println("Querying " + request.getCompleteUrl() + " ...");
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }
}
