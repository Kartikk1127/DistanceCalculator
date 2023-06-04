package com.Kartikey.distance.service;

import com.Kartikey.distance.dao.DistanceInfoRepository;
import com.Kartikey.distance.dao.PincodeRepository;
import com.Kartikey.distance.model.DistanceInfo;
import com.Kartikey.distance.model.Pincode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.Duration;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DistanceService {

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private DistanceInfoRepository distanceInfoRepository;

    private final GeoApiContext geoApiContext;

    private final Cache<String, DistanceInfo> caffeineCache;


//    @Value("${maps.api.key}")
//    public String mapKey;

    // Constructor to initialize the Google Maps API Client
    public DistanceService() {
        geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyBHXNCPza3Xo97z8MNm0a5Av9F2nxsa2lk")
                .build();
        caffeineCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // Cache entry expiration time
                .maximumSize(100) // Maximum number of entries in the cache
                .build();
    }

    private double getLatitudeFromPincode(String pincode) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBHXNCPza3Xo97z8MNm0a5Av9F2nxsa2lk")
                .build();

        GeocodingResult[] results;
        try {
            results = GeocodingApi.geocode(context, pincode).await();
            if (results.length > 0) {
                return results[0].geometry.location.lat;
            }
        } catch (Exception e) {
            // Handle exception
        }

        return 0; // Return a default value if latitude is not found
    }

    private double getLongitudeFromPincode(String pincode) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBHXNCPza3Xo97z8MNm0a5Av9F2nxsa2lk")
                .build();

        GeocodingResult[] results;
        try {
            results = GeocodingApi.geocode(context, pincode).await();
            if (results.length > 0) {
                return results[0].geometry.location.lng;
            }
        } catch (Exception e) {
            // Handle exception
        }

        return 0; // Return a default value if latitude is not found
    }





public DistanceInfo calculateDistance(String fromPincode, String toPincode) {


        //fetching latitudes and longitudes
        double fromPincodeLatitude =  getLatitudeFromPincode(fromPincode);
        double toPincodeLatitude = getLatitudeFromPincode(toPincode);

        //fetching longitudes
    double fromPincodeLongitude = getLongitudeFromPincode(fromPincode);
    double toPincodeLongitude = getLongitudeFromPincode(toPincode);

    //saving from details
    savePincodeData(fromPincode, fromPincodeLatitude, fromPincodeLongitude);

        // Check if the distance info is available in the cache
        DistanceInfo cachedDistanceInfo = getCachedDistanceInfo(fromPincode, toPincode);
        if (cachedDistanceInfo != null) {
            System.out.println("fetched from cache");
            return cachedDistanceInfo;
        }

        // Check if the distance info is available in the database
        DistanceInfo distanceInfoFromDB = distanceInfoRepository.findByFromPincodeAndToPincode(fromPincode, toPincode);
        if (distanceInfoFromDB != null) {
            System.out.println("fetched from databse");
            // Cache the distance info
            cacheDistanceInfo(distanceInfoFromDB);

            return distanceInfoFromDB;
        }

        // Fetch distance and route from Google Maps API
        DistanceMatrixApiRequest request = DistanceMatrixApi.getDistanceMatrix(geoApiContext,
                new String[]{fromPincode},
                new String[]{toPincode});
        try {
            System.out.println("fetching from maps api");
            DistanceMatrix distanceMatrix = request.await();
            if (distanceMatrix.rows.length > 0 && distanceMatrix.rows[0].elements.length > 0) {
                Distance distance = distanceMatrix.rows[0].elements[0].distance;
                Duration duration = distanceMatrix.rows[0].elements[0].duration;
                String route = distanceMatrix.rows[0].elements[0].toString();

                // Create a new DistanceInfo object
                DistanceInfo distanceInfo = new DistanceInfo();
                distanceInfo.setFromPincode(fromPincode);
                distanceInfo.setToPincode(toPincode);
                distanceInfo.setDistance(distance.inMeters);
                distanceInfo.setDuration(duration.inSeconds);
                distanceInfo.setRoute(route);

                // Save the distance info to the database
                distanceInfoRepository.save(distanceInfo);

                // Cache the distance info
                cacheDistanceInfo(distanceInfo);

                return distanceInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Handle the case when distance information cannot be retrieved

    }

    public void savePincodeData(String pincode, double latitude, double longitude)
    {
        Pincode pincodeInfo = new Pincode();
        pincodeInfo.setPincode(pincode);
        pincodeInfo.setLatitude(latitude);
        pincodeInfo.setLongitude(longitude);

        pincodeRepository.save(pincodeInfo);
    }

    private DistanceInfo getCachedDistanceInfo(String fromPincode, String toPincode) {
        DistanceInfo distanceInfo = caffeineCache.getIfPresent(getCacheKey(fromPincode, toPincode));
        return distanceInfo;
    }

    private void cacheDistanceInfo(DistanceInfo distanceInfo) {
        String cacheKey = getCacheKey(distanceInfo.getFromPincode(), distanceInfo.getToPincode());
        caffeineCache.put(cacheKey, distanceInfo);
    }

    private String getCacheKey(String fromPincode, String toPincode) {
        return fromPincode + "-" + toPincode;
    }
}
