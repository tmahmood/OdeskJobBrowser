package tmn.freelance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;

import android.widget.TextView;
import android.widget.Toast;

public class SearchJobs extends ListActivity implements OnScrollListener {

	public static final String CATEGORY = "tmn.freelance.category";
	public static final String SUBCATEGORY = "tmn.freelance.subcategory";
	public static final String SEARCHQUERY = "tmn.freelance.searchquery";

	String subcategory;
	String category;
	String searchquery;
	int startfrom;
	int endsat;

	ArrayList<JobObject> jobs = new ArrayList<JobObject>();

	String cachefile;

	HttpClient client;
	TextView tv;
	String cid;
	ProgressDialog pd;
	int error_state = 0;
	String odesk_url;

	final int MAXFILEAGE = 1800000;

	Intent jdetailinfo = new Intent();
	private JobListAdaptor adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		client = new DefaultHttpClient();

		category = getIntent().getStringExtra(CATEGORY);
		subcategory = getIntent().getStringExtra(SUBCATEGORY);
		searchquery = getIntent().getStringExtra(SEARCHQUERY);
		startfrom = 0;
		endsat = 20;

		fill_with_jobs();
		jdetailinfo.setClass(this, JobDetails.class);

	}

	private void fill_with_jobs() {
		odesk_url = get_api_url();
		cachefile = get_file_name();

		Log.i("FNAME", cachefile);

		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.progress_dialog_msg));
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();

		if (!hasExternalStoragePrivateFile()) {
			new GetJobs().execute("");

		} else {
			Log.i("ExternalStorage", "Reading File: " + cachefile);
			showJobs(getExternalStoragePrivateFile());
		}

	}

	private String get_file_name() {
		return (searchquery + category + subcategory
				+ String.valueOf(startfrom) + String.valueOf(endsat)).replace(
				' ', '_').replace('&', '_');
	}

	public String get_api_url() {
		String odesk_url = getString(R.string.odesk_api);

		ArrayList<String> ad = new ArrayList<String>();

		if (!searchquery.equals("")) {
			ad.add("q=" + searchquery.replace("&", "%26"));
		}

		if (!category.equals("All")) {
			ad.add("c1=" + category.replace("&", "%26"));

		}

		if (!subcategory.equals("All")) {
			ad.add("c2=" + subcategory.replace("&", "%26"));
		}

		for (Iterator<String> opt = ad.iterator(); opt.hasNext();) {
			String q = (String) opt.next();
			odesk_url += q + "&";
		}

		odesk_url += "page=" + startfrom + ";" + endsat;

		odesk_url = odesk_url.replace(' ', '+');

		Log.i("URL", odesk_url);
		return odesk_url;

	}

	public void showToast(String msg) {
		Toast.makeText(this, msg, 4000).show();
	}

	void prep_json_object(String searchresult) {

		JSONObject jsonResponse = null;
		JSONArray alljobs = null;

		try {
			jsonResponse = new JSONObject(searchresult);

			alljobs = jsonResponse.getJSONObject("jobs").getJSONArray("job");

			JobObject job = null;
			if (alljobs == null) {
				return;
			}

			int length = alljobs.length();
			for (int i = 0; i < length; i++) {
				job = new JobObject();
				job.setJob(alljobs.getJSONObject(i));
				jobs.add(job);
			}		

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class GetJobs extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String searchresult;

			try {

				HttpGet getCall = new HttpGet(odesk_url);

				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				searchresult = client.execute(getCall, responseHandler);

				createExternalStoragePrivateFile(searchresult);

				Log.i("ExternalStorage", "Writing File: " + cachefile);

				return searchresult;

			} catch (Exception t) {
				Log.e(SEARCHQUERY, t.toString());
				error_state = 2;
			}
			return null;
		}

		protected void onPostExecute(String result) {
			showJobs(result);
		}
	}

	public void showJobs(String searchresult) {

		if (searchresult != null) {

			prep_json_object(searchresult);
			if (adapter == null) {
				adapter = new JobListAdaptor(this, R.layout.jobshortview, jobs);
				setListAdapter(adapter);
				getListView().setOnScrollListener(this);
			} else {
				adapter.count += endsat;
				adapter.notifyDataSetChanged();
			}

		} else {
			showToast("No jobs found");

			startfrom -= endsat;
			if (startfrom < 0) {
				startfrom = 0;
			}

		}

		pd.dismiss();
	}

	void createExternalStoragePrivateFile(String txt) {
		// Create a path where we will place our private file on external
		// storage.
		File file = new File(getExternalFilesDir(null), cachefile);
		Log.d("FPATH", file.getAbsolutePath());

		try {
			OutputStream os = new FileOutputStream(file);
			os.write(txt.getBytes());
			os.close();

		} catch (IOException e) {
			Log.w("ExternalStorage", "Error writing " + file, e);

		}
	}

	class JobListAdaptor extends ArrayAdapter<JobObject> {
		private Context context;
		public int count;

		public int getCount() {
			return count;
		}

		public JobObject getItem(int pos) {
			return jobs.get(pos);
		}

		public long getItemId(int pos) {
			return pos;
		}

		public JobListAdaptor(Context context, int listresource,
				ArrayList<JobObject> j) {
			super(context, listresource, j);
			this.context = context;
			jobs = j;
			count = j.size();
		}

		@Override
		public View getView(int position, View row, ViewGroup parent) {

			JobObject job = jobs.get(position);

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				row = inflater.inflate(R.layout.jobshortview, null);

				row.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						int id = v.getId();
						JobObject cjob = jobs.get(id);
						jdetailinfo.putExtra("job", cjob);
						startActivity(jdetailinfo);
					}
				});
			}

			row.setId(position);

			TextView heading = (TextView) row.findViewById(R.id.jobheading);
			TextView details = (TextView) row.findViewById(R.id.jobdetails);
			TextView shortdesc = (TextView) row.findViewById(R.id.jobshortdesc);
			TextView activity = (TextView) row.findViewById(R.id.jobactivity);
			TextView amount = (TextView) row.findViewById(R.id.jobamount);

			if (job != null) {

				if (job.getType().equals("Fixed")) {
					shortdesc.setTextColor(0xdc564410);

				} else {
					shortdesc.setTextColor(0xd40055ff);

				}

				heading.setText(job.getTitle());
				details.setText(job.getJobShortDetail());
				shortdesc.setText(job.getJobType());
				activity.setText(job.getActivity());

				String amnt = job.getAmount();
				amount.setText("Est. Budget: "
						+ (amnt.equals("") ? "not specified" : amnt));

			}

			return row;
		}
	}

	public void onScroll(AbsListView view, int firstVisible, int visibleCount,
			int totalCount) {

		boolean loadMore = firstVisible + visibleCount >= totalCount;

		if (loadMore) {

			startfrom += endsat;
			fill_with_jobs();
		}

	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	String getExternalStoragePrivateFile() {
		// Create a path where we will place our private file on external
		// storage.

		File f = new File(getExternalFilesDir(null), cachefile);

		long n = f.length();
		char[] buf = new char[(int) n + 1];
		try {
			FileReader fr = new FileReader(f);
			int size = fr.read(buf);
			buf[size] = '\0';
			StringBuilder sb = new StringBuilder();
			sb.append(buf);
			return sb.toString();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	void deleteExternalStoragePrivateFile() {
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		File file = new File(getExternalFilesDir(null), cachefile);
		if (file != null) {
			file.delete();
		}
	}

	boolean hasExternalStoragePrivateFile() {
		// Get path for the file on external storage. If external
		// storage is not currently mounted this will fail.
		if (cachefile == null) {
			return false;
		}
		File file = new File(getExternalFilesDir(null), cachefile);
		long lastmodified = file.lastModified();

		Log.d("FPROP", String.valueOf(lastmodified));

		if (lastmodified + MAXFILEAGE < System.currentTimeMillis()) {
			file.delete();
			return false;
		}

		return file.exists();
	}

}
