package com.bignerdranch.android.splash;

import android.graphics.PointF;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lorena on 20/12/2016.
 */

public class KMLParser {
    // We don’t use namespaces
    private static final String ns = null;

    public List<Placemark> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readKml(parser);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return null;
    }

    private List readKml(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList(); // TODO: Use generics
        parser.require(XmlPullParser.START_TAG, ns, "kml");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the placemark tag
            if (name.equals("Placemark")) {
                entries.add(readPlacemark(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Placemark {
        public final String name;
        public final String description;
        public final PointF point;

        private Placemark(String name, String description, PointF point) {
            this.name = name;
            this.description = description;
            this.point = point;
        }
    }

    private Placemark readPlacemark(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Placemark");

        String name = null;
        String description = null;
        PointF point = new PointF(0, 0);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String denom = parser.getName();

            if (denom.equals("name")) {
                name = readName(parser);
            } else if (denom.equals("description")) {
                description = readDescription(parser);
            } else if (denom.equals("Point")) {
                point = readPoint(parser);
            }
        }
        return new Placemark(name, description, point);
    }

    // Processes name tags in the feed.
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");

        return name;
    }

    // Processes description tags in the feed.
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");

        return description;
    }

    // Processes point tags in the feed.
    private PointF readPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Point");
        parser.nextTag();
        String brut = readText(parser);
        PointF coords = new PointF(0, 0);
        String[] coordxy = brut.split(",");
        coordxy[1] = coordxy[1].trim();
        coords.x = Float.parseFloat(coordxy[1]);
        coords.y = Float.parseFloat(coordxy[0]);

        parser.require(XmlPullParser.END_TAG, ns, "coordinates");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "Point");

        return coords;
    }

    // For the tags name, description and point, extracts their text values .
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private String skip(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
        return null;
    }
}