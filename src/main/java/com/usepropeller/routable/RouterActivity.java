package com.usepropeller.routable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class RouterActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    Intent intent = getIntent();
	    String url;

	    Bundle extras = intent.getExtras();
	    if (extras.containsKey("url")) {
	    	url = extras.getString("url");
	    }
	    else {
		    Uri data = intent.getData();
		    String protocol = data.getScheme() + "://";
		    url = data.toString().replaceFirst(protocol, "");
		    if (Router.Companion.sharedRouter().getRootUrl() != null) {
			    Router.Companion.sharedRouter().open(Router.Companion.sharedRouter().getRootUrl());
		    }
	    }

	    Router.Companion.sharedRouter().open(url);

	    setResult(RESULT_OK, null);
	    finish();
	}
}
