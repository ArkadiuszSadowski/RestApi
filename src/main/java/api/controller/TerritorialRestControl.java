package api.controller;

import api.model.TerritorialDetails;
import api.service.ITerritorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tmc/api")
public class TerritorialRestControl {

    @Autowired
    ITerritorialService service;

    @RequestMapping(value = "/lat/{lat}/lng/{lng}/", method = RequestMethod.GET)
    public ResponseEntity<TerritorialDetails> getTerDetails(@PathVariable("lat") Double latitude, @PathVariable("lng") Double lontittude) {
        try {
            return new ResponseEntity<>(service.getTerritorialDivison(latitude, lontittude), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
