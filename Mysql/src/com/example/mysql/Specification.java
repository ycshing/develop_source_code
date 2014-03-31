package com.example.mysql;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Specification extends Activity {
	private TextView t1,t2,t3; 
	private Context context = Specification.this;
	private ProgressDialog pd;
	private String mt,result,pn;
	private InputStream input;
	private String[] spec= new String[3] ;
	private String tmp = "ddrmpartno";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.specification);
		t1 = (TextView) findViewById(R.id.partno);
		t2 = (TextView) findViewById(R.id.name);
		t3 = (TextView) findViewById(R.id.power);
		mt = getIntent().getStringExtra("motortype");
		if(mt.equals(tmp)){
			mt = "ddrm";
		}else{
			mt = "vcm";
		}
		pn = getIntent().getStringExtra("partno");
		new dcn().execute();
		
		
	}
	
private class dcn extends AsyncTask<Void, Void, Void> {

		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = ProgressDialog.show(context,"Loading", "");
			
		}


		
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			HttpClient hc = new DefaultHttpClient();
			HttpPost hp = new HttpPost("http://192.168.0.101/motordatabase/db.php");
			
			try{
				
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("motortype", mt));
				Log.i("motortype", mt);
				hp.setEntity(new UrlEncodedFormEntity(nvp));
				HttpResponse hr = hc.execute(hp);
				HttpEntity he = hr.getEntity();
				input = he.getContent();
				Log.i("success", "httpconnection");
			}catch(Exception e){
				Log.i("error", "httpconnection"+ e.toString());
			}
			//get data
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(
                        input, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String tmp="0";
				sb.append(reader.readLine() + "\n");
				while((tmp = reader.readLine())!=null){
					sb.append(tmp+"\n");
				}
				input.close();
				result = sb.toString();
				Log.i("success", "getdata");
			}catch(Exception e0){
				Log.i("error", "getdata"+e0.toString());
			}
			//json to arraylist
			try {
                JSONArray jArray=new JSONArray(result);
                for(int i=0;i<jArray.length();i++)
                {
                	JSONObject obj=jArray.getJSONObject(i);
                	String a = obj.getString("partno");
                	Log.i("obj", a + pn);
                	if(a.equals(pn)){
                	spec[0] = a;
                	spec[1] = obj.getString("name");
                	spec[2] = obj.getString("power");
                	t1.setText(spec[0]);
            		t2.setText(spec[1]);
            		t3.setText(spec[2]);


                	}
                	
                }


            }catch(Exception e1){
            	Log.i("error", "json "+e1.toString());
            }
			
			return null;
		}

	}

}
