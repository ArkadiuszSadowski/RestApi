package api.service;

import api.model.TerritorialDetails;

import java.net.MalformedURLException;

public interface ITerritorialService {
   TerritorialDetails getTerritorialDivison(double latitude, double longtitude) throws  Exception;
}
