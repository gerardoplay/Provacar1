package com.example.peppelt.provacar1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;

public class RequestHttpAsyncTask extends AsyncTask<JSONObject,Void,String>{

private RemoteCallListener<String> activity;
	
	public RequestHttpAsyncTask(RemoteCallListener<String> activity){
		this.activity = activity;
	}
	
	/**
	 * (Error) se ci sono problemi nell'invio della httprequest
	 * 	 
	 */
	@Override
	protected String doInBackground(JSONObject... params) {
		// TODO Auto-generated method stub

		HttpPost request;
		String result = "ERROR";
		try {
			request = new HttpPost(params[0].getString("url"));

		params[0].remove("url");
		StringEntity requestEntity = new StringEntity(params[0].toString());
		requestEntity.setContentType("application/json");
        HttpClient httpclient=ConnessioneHttp.getConnessione();
        request.setEntity(requestEntity);
        
		try {
	        ResponseHandler<String> handler = new BasicResponseHandler();
			result = httpclient.execute(request, handler);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       	return result;
		
		
	}
	@Override
	protected void onPostExecute(String result){
		activity.onRemoteCallListenerComplete(result);
	}

}
