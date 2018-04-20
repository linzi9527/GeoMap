package com.test.utils;


import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;


import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.vividsolutions.jts.geom.Geometry;
/**
 * 将shp文件转换为geojson数据格式,为mapbox地图使用
 * @author 蓝眼泪
 * 2018-4-10上午11:18:22
 */

public class ShpFormatUtil {

	
	public static String shp2Json(String shpPath){
        StringBuffer sb = new StringBuffer();
      //13代表生成的几何类型的参数的保留小数位是15位，不写默认4位  
        FeatureJSON fjson = new FeatureJSON(new GeometryJSON(15)); 
        try{
            sb.append("{\"type\": \"FeatureCollection\",\"features\": ");

            File file = new File(shpPath);
            ShapefileDataStore shpDataStore = null;

            shpDataStore = new ShapefileDataStore(file.toURL());
            //设置编码
            Charset charset = Charset.forName("GBK");
            shpDataStore.setCharset(charset);
            String typeName = shpDataStore.getTypeNames()[0];
          //  System.out.println("typeName:"+typeName);
            SimpleFeatureSource featureSource = null;
            featureSource =  shpDataStore.getFeatureSource (typeName);
            SimpleFeatureCollection result = featureSource.getFeatures();
            SimpleFeatureIterator itertor = result.features();
            JSONArray array = new JSONArray();
            while (itertor.hasNext())
            {
                SimpleFeature feature = itertor.next();
                Geometry geo = (Geometry) feature.getAttribute("the_geom");
                //System.out.println("geo:"+geo.getGeometryType()+","+geo.getCoordinate());
                StringWriter writer = new StringWriter();
                fjson.writeFeature(feature, writer);
                JSONObject json = new JSONObject(writer.toString());
                array.put(json);
            }
            itertor.close();
            sb.append(array.toString());
            sb.append("}");
        }
        catch(Exception e){
            e.printStackTrace();

        }
        System.out.println(sb.toString());
        return sb.toString();
    }
	
/*	public static void main(String[] args) {
		String shpPath ="D:\\eclipse-jee-indigo-SR2-win32-x86_64\\shps\\second.shp";
		ShpFormatUtil.shp2Json(shpPath);
	}*/
}

