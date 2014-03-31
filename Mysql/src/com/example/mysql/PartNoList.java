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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PartNoList extends Activity {
	private ListView list;
	private Context context = PartNoList.this;
	private String mt,result,pn;
	private ProgressDialog pd;
	private InputStream input;
	private ArrayList<String> pnlst = new ArrayList<String>();

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partnolist);
		list = (ListView) findViewById(R.id.listView1);
		mt = getIntent().getStringExtra("motortype");
		Log.i("success", mt);
		new dcn().execute();
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                pnlst );
		list.setAdapter(arrayAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Object o = arg0.getAdapter().getItem(arg2);
				pn = o.toString();
				Intent intent = new Intent(PartNoList.this, Specification.class);
				intent.putExtra("partno", pn);
				Log.i("pn value", pn);
				intent.putExtra("motortype", mt);
				Log.i("mt value", mt);
				
				startActivity(intent);
				
			}
		});
		
		
		
		
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
                	pnlst.add(a);
                }


            }catch(Exception e1){
            	Log.i("error", "json "+e1.toString());
            }
			
			return null;
		}

	}


}

