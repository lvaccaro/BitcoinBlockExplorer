package com.vaccarostudio.bitcoinblockexplorer.ipapi;

import org.json.JSONObject;

import java.util.LinkedHashMap;


/**
 * Created by luca on 26/03/2017.
 */

public class Hostplace {

    public String country;
    public String countryCode;
    public String region;
    public String regionName;
    public String city;
    public String zip;
    public String lat;
    public String lon;
    public String timezone;
    public String isp;
    public String org;
    public String as;
    public String query;

    /*
    * "status": "success",
  "country": "United States",
  "countryCode": "US",
  "region": "CA",
  "regionName": "California",
  "city": "San Francisco",
  "zip": "94105",
  "lat": "37.7898",
  "lon": "-122.3942",
  "timezone": "America\/Los_Angeles",
  "isp": "Wikimedia Foundation",
  "org": "Wikimedia Foundation",
  "as": "AS14907 Wikimedia US network",
  "query": "208.80.152.201"*/


    public static Hostplace getJson(JSONObject json) {
        Hostplace h = new Hostplace();
        try {
            h.country = json.getString("country");
            h.countryCode = json.getString("countryCode");
            h.region = json.getString("region");
            h.regionName = json.getString("regionName");
            h.city = json.getString("city");
            h.zip = json.getString("zip");
            h.lat = json.getString("lat");
            h.lon = json.getString("lon");
            h.timezone = json.getString("timezone");
            h.isp = json.getString("isp");
            h.org = json.getString("org");
            h.as = json.getString("as");
            h.query = json.getString("query");
        } catch (Exception e) {
        }
        return h;
    }


    public LinkedHashMap<String, String> toDataset() {
        LinkedHashMap<String, String> mDataset = new LinkedHashMap<>();
        mDataset.put("country", this.country);
        mDataset.put("countryCode", this.countryCode);
        mDataset.put("region", this.region);
        mDataset.put("regionName", this.regionName);
        mDataset.put("city", this.city);
        mDataset.put("zip", this.zip);
        mDataset.put("lat", this.lat);
        mDataset.put("lon", this.lon);
        mDataset.put("timezone", this.timezone);
        mDataset.put("isp", this.isp);
        mDataset.put("org", this.org);
        mDataset.put("as", this.as);
        mDataset.put("query", this.query);
        return mDataset;
    }
}