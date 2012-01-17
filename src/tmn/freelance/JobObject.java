package tmn.freelance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;



public class JobObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private String jobShortDetail = null;
	HashMap<String, String> jobdata = new HashMap<String, String>();
	private boolean fixed = false;
	private String budgetkey;

	HashMap<String, String> pref = new HashMap<String, String>();
	
	
	public void setJob(JSONObject job) {
		try {
			Iterator<?> iterator = job.keys();
			while (iterator.hasNext()) {
				String k = (String) iterator.next();
				jobdata.put(k, job.getString(k));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (getByKey("job_type").equals("Fixed")) {
			fixed = true;
			budgetkey = "amount";
		} else {
			budgetkey = "op_pref_hourly_rate_max";
		}
		
		pref.put("op_pref_fb_score", "Feedback Score");
		pref.put("op_pref_test_name", "Test Name");
		pref.put("op_pref_test", "Test");
		pref.put("op_pref_hours_per_week", "Hours/Week");
		pref.put("op_pref_location", "Location");
		pref.put("op_pref_odesk_hours", "Odesk hours");
		pref.put("op_pref_english_skill", "English");
		pref.put("op_pref_has_portfolio", "Portfolio");

	}

	public boolean fixedPrice() {
		return fixed;
	}

	public String getByKey(String key) {
		return jobdata.get(key);
	}

	public String getTimeZone() {
		return getByKey("timezone");
	}

	public String getJobShortDetail() {
		
		// better way to create short detail
		if (jobShortDetail == null) {
			
			jobShortDetail = String.format(
						"%s", getRequiredSkills()
						);		

		}
		return jobShortDetail;
	}
	
	public String getAmount() {
		String amount = getByKey("amount");
		if (!amount.equals("")) {
			return "$" + amount;
		}
		return amount;
	}

	public String getCategory() {
		return getByKey("job_category_level_one");
	}

	public String getSubCategory() {
		return getByKey("job_category_level_two");
	}

	public String getTimeCreated() {
		return String.format("%s %s", getByKey("op_time_created"),
				getByKey("op_date_created"));
	}

	public String getApplyLink() {
		String base_url = "https://www.odesk.com/login.php?redir=https%3A%2F%2Fwww.odesk.com%2Foffers%2F%3F";
		
		return base_url + "do%3Dnew%26job__reference%3D" + getByKey("op_recno");
				
	}

	public String getCountry() {
		return getByKey("op_country");
	}

	public String getJobPage() {
		return "https://www.odesk.com/jobs/" + getByKey("ciphertext");

	}

	public String getActivity() {
		return getByKey("op_engagement");

	}

	public String getRequiredSkills() {
		String eng = getByKey("op_required_skills");
		if (eng == null || eng.equals("")) {
			return "Not specified";
		}
		eng = eng.replace(",", ", ");
		return eng;
	}

	public String getShortDescription() {
		return getByKey("op_desc_digest");
	}

	public String getBudget() {
		String val;
		val = getByKey(budgetkey);
		
		if (val.equals("0") || val.equals("")) {
			return null;
		} else {
			if (fixed) {
				return "$" + val;
			} else {
				return String.format("$%s/hour", val);
			}
		}
	}
	
	public String getLabel(String prefkey) {
		
		return pref.get(prefkey);
	}
	
	public  ArrayList<String> getPreferences() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		String tmp;
		for (String s : pref.keySet()) {
			tmp = getByKey(s);
			if (tmp.equals("") || tmp.equals("0")) {
				continue;
			}			
			list.add(s);			
		}
		
		return list;
	}

	public String getPreferredFeedbackScore() {
		return getByKey("op_pref_fb_score");
	}

	public String getJobType() {
		return getByKey("job_type");
	}

	public String getTitle() {
		return getByKey("op_title");
	}

	public String getDescription() {
		return getByKey("op_description");
	}

	public String getMaxHourlyBid() {
		return getByKey("op_high_hourly_rate_active");
	}

	public String getLowestHourlyBid() {
		return getByKey("op_low_hourly_rate_active");
	}

	public String getType() {
		return getByKey("job_type");
	}
}
