package api.service;


import api.model.TerritorialDetails;
import com.vividsolutions.jts.geom.*;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class TerritorialService implements ITerritorialService {
    private final String CHARSET = "ISO-8859-2";
    private final Filter filter = Filter.INCLUDE;

    private final CoordinateReferenceSystem dataCRS;

    private final FeatureCollection<SimpleFeatureType, SimpleFeature> jednEwidCollection;
    private final FeatureCollection<SimpleFeatureType, SimpleFeature> gminyCollection;
    private final FeatureCollection<SimpleFeatureType, SimpleFeature> powiatyCollection;
    private final FeatureCollection<SimpleFeatureType, SimpleFeature> wojCollection;
    private final FeatureCollection<SimpleFeatureType, SimpleFeature> obCollection;

    /*
    * pliki pobrane z https://gis-support.pl/baza-wiedzy/dane-do-pobrania/
    *
    * jednostka ewidencyjna a obreb http://www.eko.org.pl/lkp/prawo_html/rozporz_ewidencja_gruntow.html
     */
    public TerritorialService() throws Exception {
        jednEwidCollection = createNewFeatureCollection(new File("").getAbsolutePath() + "/src/main/resources/jednostki_ewidencyjne.shp");
        dataCRS = jednEwidCollection.getSchema().getCoordinateReferenceSystem();

        gminyCollection = createNewFeatureCollection(new File("").getAbsolutePath() + "/src/main/resources/gminy.shp");
        powiatyCollection = createNewFeatureCollection(new File("").getAbsolutePath() + "/src/main/resources/powiaty.shp");
        wojCollection = createNewFeatureCollection(new File("").getAbsolutePath() + "/src/main/resources/wojewodztwa.shp");
        obCollection = createNewFeatureCollection(new File("").getAbsolutePath() + "/src/main/resources/obreby_ewidencyjne.shp");
    }

    private FeatureCollection<SimpleFeatureType, SimpleFeature> createNewFeatureCollection(String path) throws Exception {
        File collectionFile = new File(path);

        Map<String, Object> collectionMap = new HashMap<>();
        collectionMap.put("url", collectionFile.toURI().toURL());
        collectionMap.put("charset", CHARSET);

        DataStore dataStore = DataStoreFinder.getDataStore(collectionMap);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> jednEwidSource = dataStore.getFeatureSource(typeName);
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = jednEwidSource.getFeatures(filter);
        return collection;
    }

    @Override
    public TerritorialDetails getTerritorialDivison(double latitude, double longtitude) throws Exception {

        TerritorialDetails td = new TerritorialDetails();
        td.setLat(String.valueOf(latitude));
        td.setLng(String.valueOf(longtitude));

        Thread[] workers = new Thread[4];

        workers[0] = new Thread(() -> {
            try {
                String obResult = getAttributeValue(longtitude, latitude, obCollection);
                String jednResult = getAttributeValue(longtitude, latitude, jednEwidCollection);
                td.setJednostkaEwidencyjna(jednResult);
                td.setObrebEwidencyjny(obResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        workers[1] = new Thread(() -> {
            try {
                td.setGmina(getAttributeValue(longtitude, latitude, gminyCollection));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        workers[2] = new Thread(() -> {
            try {
                String powiatResult = getAttributeValue(longtitude, latitude, powiatyCollection);
                td.setPowiat(StringUtils.capitalize(powiatResult));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        workers[3] = new Thread(() -> {
            try {
                String wojewodztwoResult = getAttributeValue(longtitude, latitude, wojCollection);
                td.setWojewodztwo(StringUtils.capitalize(wojewodztwoResult));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        for (int i = 0; i < 4; i++)
            workers[i].start();

        for (int i = 0; i < 4; i++)
            workers[i].join();


        return td;
    }

    private String getAttributeValue(double longtitude, double latitude, FeatureCollection<SimpleFeatureType, SimpleFeature> collection) throws Exception {
        FeatureIterator<SimpleFeature> features = collection.features();
        try {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                MathTransform transform = CRS.findMathTransform(dataCRS, DefaultGeographicCRS.WGS84, true);
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Geometry geometry2 = JTS.transform(geometry, transform);

                MultiPolygon multiPolygon1 = (MultiPolygon) geometry2;

                GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
                Coordinate coord = new Coordinate(longtitude, latitude);
                Point point = geometryFactory.createPoint(coord);
                if (multiPolygon1.contains(point)) {
                    String attributeValue = (String) feature.getAttribute("jpt_nazwa_");
                    return attributeValue;
                }
            }
        } catch (Exception ex) {
            throw new Exception("Nie znaleziono podanych współrzędnych");
        } finally {
            features.close();
        }
        throw new Exception("Nie znaleziono podanych współrzędnych");
    }
}
