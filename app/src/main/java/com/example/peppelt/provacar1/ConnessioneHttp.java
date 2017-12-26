package com.example.peppelt.provacar1;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;

public class ConnessioneHttp {
	private static long secure=0;
	private static DefaultHttpClient httpclient=null;
	private Context context;
	public ConnessioneHttp(Context context){
		this.context=context;
	}
	public synchronized static HttpClient getConnessione(){

		if(httpclient==null){

			httpclient = new DefaultHttpClient(); 	
		}
		return httpclient;
	}
	public synchronized static void setSecure(long sec){
		secure=sec;
	}
	public synchronized static long getSecure(){
		return secure;
	}
}
