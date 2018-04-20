package com.test.map;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;

public class Shp2GeoJson {

	 /** 
     * shp转换为Geojson 
     * @param shpPath 
     * @return 
     */  
    public static String shape2Geojson(String shpPath){  
    	//13代表生成的几何类型的参数的保留小数位是15位，不写默认4位  
        FeatureJSON fjson = new FeatureJSON(new GeometryJSON(15));  
          
        try{  
            StringBuffer sb = new StringBuffer();  
            sb.append("{\"type\": \"FeatureCollection\",\"features\": ");  
              
            File file = new File(shpPath);  
            ShapefileDataStore shpDataStore =null;  
              
            shpDataStore = new ShapefileDataStore(file.toURL());  
            //设置编码  
            Charset charset = Charset.forName("GBK");  
            shpDataStore.setCharset(charset);  
            String typeName = shpDataStore.getTypeNames()[0];  
            SimpleFeatureSource featureSource = null;  
            featureSource =  shpDataStore.getFeatureSource (typeName);  
            SimpleFeatureCollection result = featureSource.getFeatures();  
            SimpleFeatureIterator itertor = result.features();  
            JSONArray array = new JSONArray();  
            while (itertor.hasNext())  
            {  
                SimpleFeature feature = itertor.next();  
                StringWriter writer = new StringWriter();  
                fjson.writeFeature(feature, writer);  
                JSONObject json = new JSONObject(writer.toString());  
                array.put(json);  
            }  
            itertor.close();  
            sb.append(array.toString());  
            sb.append("}");  
              
            //写入文件  
            System.out.println(sb.toString());
            return sb.toString();  
        }  
        catch(Exception e){  
            e.printStackTrace();  
              
        }  
        return null;  
    }  
      
	// 获取position经纬度坐标 转成 xyz 坐标
    /**
     * var earthRadius = 6367; //radius in km
     * @param lng
     * @param lat
     * @param alt
     * @return
     */
    public static String getPosition(double lng, double lat, double alt) {
    	
    	double phi = (90 - lat) * (Math.PI / 180);
    	double theta = (lng + 180) * (Math.PI / 180);
    	double radius = alt + 200;

    	double x = -(radius * Math.sin(phi) * Math.cos(theta));
    	double z = (radius * Math.sin(phi) * Math.sin(theta));
    	double y = (radius * Math.cos(phi));

		return "{x: "+x+",y: "+y+",z: "+z+"}";
	}
    
    /**
     * 经纬度转xyz
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径
     */
    public static String  lglt2xyz(double longitude,double latitude,double radius){
    	double lg = degtoRad(longitude);
    	double lt = degtoRad(latitude);
    	
    	double  y = radius * Math.sin(lt);
    	double temp = radius * Math.cos(lt);
    	double x = temp * Math.sin(lg);
    	double z = temp * Math.cos(lg);
     
        return "{x: "+x+",y: "+y+",z: "+z+"}";
    }
    
    /** 
     * Convert degrees to radian 
     * 转换弧度
     * @param val Value to convert  
     */  
    public static double degtoRad(double val){  
        return val*Math.PI/180;  
    }  
   
    //根据两个点坐标计算它们之间的距离（按照圆球体计算，粗略计算）  
    public static double getDistance(double y1, double x1, double y2, double x2)  
    {  
    	double earthRadius = 6367; //radius in km
        double rady1 = degtoRad(y1);  
        double rady2 = degtoRad(y2);  
        double  a = rady1 - rady2;  
        double  b = degtoRad(x1)-degtoRad(x2);  
        double  s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(rady1)*Math.cos(rady2)*Math.pow(Math.sin(b/2),2)));  
		        s = s * earthRadius;  
		        s = (double) (Math.round(s * 10000) / 10000);  
        return  s;  
    }  
    
    /** 
     * 工具类测试方法 
     * @param args 
     */  
    public static void main(String[] args){  
    	String shpPath ="D:\\eclipse-jee-indigo-SR2-win32-x86_64\\shps\\second.shp"; 
        shape2Geojson(shpPath);  
    	//double r=6367*1000;{x: 4386606.696332554,y: 4074510.531279422,z: -2166733.629725508}
    	//double r=6367;{x: 4386.606696332555,y: 4074.510531279422,z: -2166.7336297255083}
    	//  double r=636.7;
    	//{x: 68.8959744987051,y: 63.994197130193534,z: -34.030683677171474}
    	//System.out.println(lglt2xyz(116.28674621748917,39.78749258193983,r));
    }  
    
    
}
