package es.sunflower;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GPSMockChecker extends CordovaPlugin{

    private JSONObject objGPS = new JSONObject();
    private GPSMockChecker mContext;
    private static JSONArray whiteList;

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        mContext = this;
        whiteList = data;
        if (action.equals("check")) {
            objGPS = new JSONObject();
            if (android.os.Build.VERSION.SDK_INT <= 22) {
                if (Secure.getString(this.cordova.getActivity().getContentResolver(), Secure.ALLOW_MOCK_LOCATION).equals("0")){
                    objGPS.put("isMock",false);
                }else{
                    objGPS.put("isMock",true);
                }

            }
            else {
                JSONArray mocks =areThereMockPermissionApps(mContext.cordova.getActivity());
                objGPS.put("isMock", mocks.length() > 0);
                if (objGPS.getBoolean("isMock")) {
                    objGPS.put("mocks",mocks);
                }
            }
            Log.i("Location", "isMock: "+objGPS.get("isMock"));
            callbackContext.success(objGPS);
            return true;
        }else {
            return false;
        }
    }

    public static JSONArray areThereMockPermissionApps(Context context) throws JSONException {
        JSONArray mocks = new JSONArray();

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        // Check for System App //
                        if(!((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)) {
                            if (requestedPermissions[i]
                                    .equals("android.permission.ACCESS_MOCK_LOCATION")
                                    && !applicationInfo.packageName.equals(context.getPackageName())
                                    && !inWhiteList(applicationInfo.packageName)) {

                                JSONObject mock = new JSONObject();
                                mock.put("package", applicationInfo.packageName);
                                mock.put("name", pm.getApplicationLabel(applicationInfo).toString());
                                mocks.put(mock);
                            }
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        return mocks;
    }

    private static boolean inWhiteList(String item) throws JSONException {
        for (int i = 0; i < whiteList.length(); i++) {
            if (whiteList.get(i).toString().equals(item)){
                return true;
            }
        }
        return false;
    }


}
