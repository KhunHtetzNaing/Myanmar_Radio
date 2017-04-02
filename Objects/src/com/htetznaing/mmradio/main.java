package com.htetznaing.mmradio;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.mmradio", "com.htetznaing.mmradio.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.mmradio", "com.htetznaing.mmradio.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.mmradio.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _ad = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _b = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper _i = null;
public de.donmanfred.IconButtonWrapper _b1 = null;
public de.donmanfred.IconButtonWrapper _b2 = null;
public de.donmanfred.IconButtonWrapper _b3 = null;
public de.donmanfred.IconButtonWrapper _b4 = null;
public de.donmanfred.IconButtonWrapper _b5 = null;
public de.donmanfred.IconButtonWrapper _b6 = null;
public de.donmanfred.IconButtonWrapper _b7 = null;
public anywheresoftware.b4a.objects.drawable.BitmapDrawable _b1b = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _ab = null;
public anywheresoftware.b4a.objects.LabelWrapper _lb = null;
public com.htetznaing.mmradio.cherry _cherry = null;
public com.htetznaing.mmradio.mandalay _mandalay = null;
public com.htetznaing.mmradio.mrtv _mrtv = null;
public com.htetznaing.mmradio.padamyar _padamyar = null;
public com.htetznaing.mmradio.shwe _shwe = null;
public com.htetznaing.mmradio.thitsar _thitsar = null;
public com.htetznaing.mmradio.tineyinthar _tineyinthar = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (cherry.mostCurrent != null);
vis = vis | (mandalay.mostCurrent != null);
vis = vis | (mrtv.mostCurrent != null);
vis = vis | (padamyar.mostCurrent != null);
vis = vis | (shwe.mostCurrent != null);
vis = vis | (thitsar.mostCurrent != null);
vis = vis | (tineyinthar.mostCurrent != null);
return vis;}
public static String  _activity_click() throws Exception{
 //BA.debugLineNum = 166;BA.debugLine="Sub Activity_Click";
 //BA.debugLineNum = 167;BA.debugLine="If I.Ready Then I.Show";
if (mostCurrent._i.getReady()) { 
mostCurrent._i.Show();};
 //BA.debugLineNum = 168;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
int _height = 0;
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 33;BA.debugLine="Activity.Color = Colors.White";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 34;BA.debugLine="ab.Initialize(Colors.Black,15)";
mostCurrent._ab.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Black,(int) (15));
 //BA.debugLineNum = 35;BA.debugLine="b1b.Initialize(LoadBitmap(File.DirAssets,\"radio.pn";
mostCurrent._b1b.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"radio.png").getObject()));
 //BA.debugLineNum = 36;BA.debugLine="b1.Initialize(\"mv\")";
mostCurrent._b1.Initialize(processBA,"mv");
 //BA.debugLineNum = 37;BA.debugLine="b1.Text = \"MRTV\"";
mostCurrent._b1.setText("MRTV");
 //BA.debugLineNum = 38;BA.debugLine="b1.IconPadding = 20";
mostCurrent._b1.setIconPadding((int) (20));
 //BA.debugLineNum = 39;BA.debugLine="b1.setIcon(True,b1b)";
mostCurrent._b1.setIcon(anywheresoftware.b4a.keywords.Common.True,(android.graphics.drawable.Drawable)(mostCurrent._b1b.getObject()));
 //BA.debugLineNum = 40;BA.debugLine="b1.Background = ab";
mostCurrent._b1.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ab.getObject()));
 //BA.debugLineNum = 41;BA.debugLine="Activity.AddView(b1, 50%x - 125dip, 2%y,250dip,50d";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b1.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125))),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (2),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 43;BA.debugLine="b2.Initialize(\"se\")";
mostCurrent._b2.Initialize(processBA,"se");
 //BA.debugLineNum = 44;BA.debugLine="b2.Text = \"Shwe FM\"";
mostCurrent._b2.setText("Shwe FM");
 //BA.debugLineNum = 45;BA.debugLine="b2.IconPadding = 20";
mostCurrent._b2.setIconPadding((int) (20));
 //BA.debugLineNum = 46;BA.debugLine="b2.setIcon(True,b1b)";
mostCurrent._b2.setIcon(anywheresoftware.b4a.keywords.Common.True,(android.graphics.drawable.Drawable)(mostCurrent._b1b.getObject()));
 //BA.debugLineNum = 47;BA.debugLine="b2.Background = ab";
mostCurrent._b2.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ab.getObject()));
 //BA.debugLineNum = 48;BA.debugLine="Activity.AddView(b2,50%x - 125dip,50dip+3%y,250di";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b2.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125))),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (3),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 50;BA.debugLine="b3.Initialize(\"c\")";
mostCurrent._b3.Initialize(processBA,"c");
 //BA.debugLineNum = 51;BA.debugLine="b3.Text = \"Cherry FM\"";
mostCurrent._b3.setText("Cherry FM");
 //BA.debugLineNum = 52;BA.debugLine="b3.IconPadding = 20";
mostCurrent._b3.setIconPadding((int) (20));
 //BA.debugLineNum = 53;BA.debugLine="b3.setIcon(True,b1b)";
mostCurrent._b3.setIcon(anywheresoftware.b4a.keywords.Common.True,(android.graphics.drawable.Drawable)(mostCurrent._b1b.getObject()));
 //BA.debugLineNum = 54;BA.debugLine="b3.Background = ab";
mostCurrent._b3.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ab.getObject()));
 //BA.debugLineNum = 55;BA.debugLine="Activity.AddView(b3,50%x - 125dip,50dip+50dip+4%y";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b3.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125))),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (4),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 57;BA.debugLine="b4.Initialize(\"pdm\")";
mostCurrent._b4.Initialize(processBA,"pdm");
 //BA.debugLineNum = 58;BA.debugLine="b4.Text = \"Padamyar FM\"";
mostCurrent._b4.setText("Padamyar FM");
 //BA.debugLineNum = 59;BA.debugLine="b4.IconPadding = 20";
mostCurrent._b4.setIconPadding((int) (20));
 //BA.debugLineNum = 60;BA.debugLine="b4.setIcon(True,b1b)";
mostCurrent._b4.setIcon(anywheresoftware.b4a.keywords.Common.True,(android.graphics.drawable.Drawable)(mostCurrent._b1b.getObject()));
 //BA.debugLineNum = 61;BA.debugLine="b4.Background = ab";
mostCurrent._b4.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ab.getObject()));
 //BA.debugLineNum = 62;BA.debugLine="Activity.AddView(b4,50%x - 125dip,50dip+ 50dip+50";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b4.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125))),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 64;BA.debugLine="b5.Initialize(\"m\")";
mostCurrent._b5.Initialize(processBA,"m");
 //BA.debugLineNum = 65;BA.debugLine="b5.Text = \"Mandalay FM\"";
mostCurrent._b5.setText("Mandalay FM");
 //BA.debugLineNum = 66;BA.debugLine="b5.IconPadding = 20";
mostCurrent._b5.setIconPadding((int) (20));
 //BA.debugLineNum = 67;BA.debugLine="b5.setIcon(True,b1b)";
mostCurrent._b5.setIcon(anywheresoftware.b4a.keywords.Common.True,(android.graphics.drawable.Drawable)(mostCurrent._b1b.getObject()));
 //BA.debugLineNum = 68;BA.debugLine="b5.Background = ab";
mostCurrent._b5.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ab.getObject()));
 //BA.debugLineNum = 69;BA.debugLine="Activity.AddView(b5,50%x - 125dip,50dip+ 50dip+50";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b5.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125))),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (6),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 71;BA.debugLine="b6.Initialize(\"tyt\")";
mostCurrent._b6.Initialize(processBA,"tyt");
 //BA.debugLineNum = 72;BA.debugLine="b6.Text = \"Tine Yin Thar\"";
mostCurrent._b6.setText("Tine Yin Thar");
 //BA.debugLineNum = 73;BA.debugLine="b6.IconPadding = 20";
mostCurrent._b6.setIconPadding((int) (20));
 //BA.debugLineNum = 74;BA.debugLine="b6.setIcon(True,b1b)";
mostCurrent._b6.setIcon(anywheresoftware.b4a.keywords.Common.True,(android.graphics.drawable.Drawable)(mostCurrent._b1b.getObject()));
 //BA.debugLineNum = 75;BA.debugLine="b6.Background = ab";
mostCurrent._b6.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ab.getObject()));
 //BA.debugLineNum = 76;BA.debugLine="Activity.AddView(b6,50%x - 125dip,50dip+ 50dip+50";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b6.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125))),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (7),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 78;BA.debugLine="b7.Initialize(\"ts\")";
mostCurrent._b7.Initialize(processBA,"ts");
 //BA.debugLineNum = 79;BA.debugLine="b7.Text = \"Thitsar Parami FM\"";
mostCurrent._b7.setText("Thitsar Parami FM");
 //BA.debugLineNum = 80;BA.debugLine="b7.IconPadding = 20";
mostCurrent._b7.setIconPadding((int) (20));
 //BA.debugLineNum = 81;BA.debugLine="b7.setIcon(True,b1b)";
mostCurrent._b7.setIcon(anywheresoftware.b4a.keywords.Common.True,(android.graphics.drawable.Drawable)(mostCurrent._b1b.getObject()));
 //BA.debugLineNum = 82;BA.debugLine="b7.Background = ab";
mostCurrent._b7.setBackground((android.graphics.drawable.Drawable)(mostCurrent._ab.getObject()));
 //BA.debugLineNum = 83;BA.debugLine="Activity.AddView(b7,50%x - 125dip,50dip+ 50dip+50";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b7.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (125))),(int) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (8),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (250)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 86;BA.debugLine="B.Initialize2(\"B\",\"ca-app-pub-4173348573252986/41";
mostCurrent._b.Initialize2(mostCurrent.activityBA,"B","ca-app-pub-4173348573252986/4119293753",mostCurrent._b.SIZE_SMART_BANNER);
 //BA.debugLineNum = 87;BA.debugLine="Dim height As Int";
_height = 0;
 //BA.debugLineNum = 88;BA.debugLine="If GetDeviceLayoutValues.ApproximateScreenSize <";
if (anywheresoftware.b4a.keywords.Common.GetDeviceLayoutValues(mostCurrent.activityBA).getApproximateScreenSize()<6) { 
 //BA.debugLineNum = 89;BA.debugLine="If 100%x > 100%y Then height = 32dip Else height";
if (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)>anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)) { 
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (32));}
else {
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50));};
 }else {
 //BA.debugLineNum = 91;BA.debugLine="height = 90dip";
_height = anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (90));
 };
 //BA.debugLineNum = 93;BA.debugLine="Activity.AddView(B, 0dip, 100%y - height, 100%x,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b.getObject()),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (0)),(int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA)-_height),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),_height);
 //BA.debugLineNum = 94;BA.debugLine="B.LoadAd";
mostCurrent._b.LoadAd();
 //BA.debugLineNum = 95;BA.debugLine="Log(B)";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(mostCurrent._b));
 //BA.debugLineNum = 98;BA.debugLine="I.Initialize(\"Interstitial\",\"ca-app-pub-417334857";
mostCurrent._i.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/5596026959");
 //BA.debugLineNum = 99;BA.debugLine="I.LoadAd";
mostCurrent._i.LoadAd();
 //BA.debugLineNum = 101;BA.debugLine="ad.Initialize(\"ad\",100)";
_ad.Initialize(processBA,"ad",(long) (100));
 //BA.debugLineNum = 102;BA.debugLine="ad.Enabled = False";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 104;BA.debugLine="Activity.AddMenuItem3(\"Share\",\"share\",LoadBitmap(F";
mostCurrent._activity.AddMenuItem3(BA.ObjectToCharSequence("Share"),"share",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"share.png").getObject()),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 106;BA.debugLine="lb.Initialize(\"lb\")";
mostCurrent._lb.Initialize(mostCurrent.activityBA,"lb");
 //BA.debugLineNum = 107;BA.debugLine="lb.Text = \"Developed By Myanmar Android App\"";
mostCurrent._lb.setText(BA.ObjectToCharSequence("Developed By Myanmar Android App"));
 //BA.debugLineNum = 108;BA.debugLine="lb.TextColor = Colors.Magenta";
mostCurrent._lb.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Magenta);
 //BA.debugLineNum = 109;BA.debugLine="lb.Gravity = Gravity.CENTER";
mostCurrent._lb.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 110;BA.debugLine="Activity.AddView(lb,0%x,80%y,100%x,10%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lb.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 111;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _answ = 0;
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _ii = null;
 //BA.debugLineNum = 215;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 216;BA.debugLine="Dim Answ As Int";
_answ = 0;
 //BA.debugLineNum = 217;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 218;BA.debugLine="Answ = Msgbox2(\"If you want to get new updates o";
_answ = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("If you want to get new updates on  Facebook? Please Like "+anywheresoftware.b4a.keywords.Common.CRLF+"Myanmar Android Apps Page!"),BA.ObjectToCharSequence("Attention!"),"Yes","","No",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"fb.png").getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 219;BA.debugLine="If Answ = DialogResponse.NEGATIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 220;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 };
 //BA.debugLineNum = 223;BA.debugLine="If Answ = DialogResponse.POSITIVE Then";
if (_answ==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 224;BA.debugLine="Try";
try { //BA.debugLineNum = 226;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 228;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb:/";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 229;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e14) {
			processBA.setLastException(e14); //BA.debugLineNum = 233;BA.debugLine="Dim ii As Intent";
_ii = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 234;BA.debugLine="ii.Initialize(ii.ACTION_VIEW, \"https://m.facebo";
_ii.Initialize(_ii.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 236;BA.debugLine="StartActivity(ii)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_ii.getObject()));
 };
 //BA.debugLineNum = 239;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 241;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 179;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 175;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 177;BA.debugLine="End Sub";
return "";
}
public static String  _ad_tick() throws Exception{
 //BA.debugLineNum = 170;BA.debugLine="Sub ad_Tick";
 //BA.debugLineNum = 171;BA.debugLine="If I.Ready Then I.Show Else I.LoadAd";
if (mostCurrent._i.getReady()) { 
mostCurrent._i.Show();}
else {
mostCurrent._i.LoadAd();};
 //BA.debugLineNum = 172;BA.debugLine="ad.Enabled = False";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 173;BA.debugLine="End Sub";
return "";
}
public static String  _c_click() throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Sub c_Click";
 //BA.debugLineNum = 114;BA.debugLine="StartActivity(Cherry)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._cherry.getObject()));
 //BA.debugLineNum = 115;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 24;BA.debugLine="Dim B As AdView";
mostCurrent._b = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim I As InterstitialAd";
mostCurrent._i = new anywheresoftware.b4a.admobwrapper.AdViewWrapper.InterstitialAdWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim b1,b2,b3,b4,b5,b6,b7 As IconButton";
mostCurrent._b1 = new de.donmanfred.IconButtonWrapper();
mostCurrent._b2 = new de.donmanfred.IconButtonWrapper();
mostCurrent._b3 = new de.donmanfred.IconButtonWrapper();
mostCurrent._b4 = new de.donmanfred.IconButtonWrapper();
mostCurrent._b5 = new de.donmanfred.IconButtonWrapper();
mostCurrent._b6 = new de.donmanfred.IconButtonWrapper();
mostCurrent._b7 = new de.donmanfred.IconButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim b1b As BitmapDrawable";
mostCurrent._b1b = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();
 //BA.debugLineNum = 28;BA.debugLine="Dim ab As ColorDrawable";
mostCurrent._ab = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 29;BA.debugLine="dim lb as Label";
mostCurrent._lb = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _i_adclosed() throws Exception{
 //BA.debugLineNum = 149;BA.debugLine="Sub I_AdClosed";
 //BA.debugLineNum = 150;BA.debugLine="I.LoadAd";
mostCurrent._i.LoadAd();
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _i_adopened() throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Sub I_adopened";
 //BA.debugLineNum = 163;BA.debugLine="Log(\"I Opened\")";
anywheresoftware.b4a.keywords.Common.Log("I Opened");
 //BA.debugLineNum = 164;BA.debugLine="End Sub";
return "";
}
public static String  _i_failedtoreceivead(String _errorcode) throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Sub I_FailedToReceiveAd (ErrorCode As String)";
 //BA.debugLineNum = 158;BA.debugLine="Log(\"I not Received - \" &\"Error Code: \"&ErrorCode";
anywheresoftware.b4a.keywords.Common.Log("I not Received - "+"Error Code: "+_errorcode);
 //BA.debugLineNum = 159;BA.debugLine="I.LoadAd";
mostCurrent._i.LoadAd();
 //BA.debugLineNum = 160;BA.debugLine="End Sub";
return "";
}
public static String  _i_receivead() throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub I_ReceiveAd";
 //BA.debugLineNum = 154;BA.debugLine="Log(\"I Received\")";
anywheresoftware.b4a.keywords.Common.Log("I Received");
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _lb_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _facebook = null;
anywheresoftware.b4a.objects.IntentWrapper _ii = null;
 //BA.debugLineNum = 196;BA.debugLine="Sub lb_Click";
 //BA.debugLineNum = 197;BA.debugLine="Try";
try { //BA.debugLineNum = 199;BA.debugLine="Dim Facebook As Intent";
_facebook = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 201;BA.debugLine="Facebook.Initialize(Facebook.ACTION_VIEW, \"fb://";
_facebook.Initialize(_facebook.ACTION_VIEW,"fb://page/627699334104477");
 //BA.debugLineNum = 202;BA.debugLine="StartActivity(Facebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_facebook.getObject()));
 } 
       catch (Exception e6) {
			processBA.setLastException(e6); //BA.debugLineNum = 206;BA.debugLine="Dim ii As Intent";
_ii = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 207;BA.debugLine="ii.Initialize(ii.ACTION_VIEW, \"https://m.faceboo";
_ii.Initialize(_ii.ACTION_VIEW,"https://m.facebook.com/MmFreeAndroidApps");
 //BA.debugLineNum = 209;BA.debugLine="StartActivity(ii)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_ii.getObject()));
 };
 //BA.debugLineNum = 212;BA.debugLine="End Sub";
return "";
}
public static String  _m_click() throws Exception{
 //BA.debugLineNum = 118;BA.debugLine="Sub m_Click";
 //BA.debugLineNum = 119;BA.debugLine="StartActivity(Mandalay)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._mandalay.getObject()));
 //BA.debugLineNum = 120;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _mv_click() throws Exception{
 //BA.debugLineNum = 123;BA.debugLine="Sub mv_Click";
 //BA.debugLineNum = 124;BA.debugLine="StartActivity(MRTV)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._mrtv.getObject()));
 //BA.debugLineNum = 125;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 126;BA.debugLine="End Sub";
return "";
}
public static String  _pdm_click() throws Exception{
 //BA.debugLineNum = 128;BA.debugLine="Sub pdm_Click";
 //BA.debugLineNum = 129;BA.debugLine="StartActivity(Padamyar)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._padamyar.getObject()));
 //BA.debugLineNum = 130;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
cherry._process_globals();
mandalay._process_globals();
mrtv._process_globals();
padamyar._process_globals();
shwe._process_globals();
thitsar._process_globals();
tineyinthar._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim ad As Timer";
_ad = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _se_click() throws Exception{
 //BA.debugLineNum = 133;BA.debugLine="Sub Se_Click";
 //BA.debugLineNum = 134;BA.debugLine="StartActivity(Shwe)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._shwe.getObject()));
 //BA.debugLineNum = 135;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
public static String  _share_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _shareit = null;
b4a.util.BClipboard _copy = null;
 //BA.debugLineNum = 183;BA.debugLine="Sub share_Click";
 //BA.debugLineNum = 184;BA.debugLine="Dim ShareIt As Intent";
_shareit = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 185;BA.debugLine="Dim copy As BClipboard";
_copy = new b4a.util.BClipboard();
 //BA.debugLineNum = 186;BA.debugLine="copy.clrText";
_copy.clrText(mostCurrent.activityBA);
 //BA.debugLineNum = 187;BA.debugLine="copy.setText(\"မိမိတို႔ဖုန္းကေနတဆင့္ ျမန္မာေရဒီယို";
_copy.setText(mostCurrent.activityBA,"မိမိတို႔ဖုန္းကေနတဆင့္ ျမန္မာေရဒီယိုလိုင္းမ်ား 'အခမဲ့' နားဆင္နိုင္တဲ့ Myanmar Radio App ေလးပါ။"+anywheresoftware.b4a.keywords.Common.CRLF+"ပါဝင္ေသာ ေရဒီယိုလိုင္းမ်ားမွာ..."+anywheresoftware.b4a.keywords.Common.CRLF+"ခ်ယ္ရီ FM"+anywheresoftware.b4a.keywords.Common.CRLF+"မႏၲေလး FM"+anywheresoftware.b4a.keywords.Common.CRLF+"ေရႊ FM"+anywheresoftware.b4a.keywords.Common.CRLF+"ပတၱျမား FM"+anywheresoftware.b4a.keywords.Common.CRLF+"MRTV"+anywheresoftware.b4a.keywords.Common.CRLF+"သစၥာပါရမီ"+anywheresoftware.b4a.keywords.Common.CRLF+"တိုင္းရင္းသား"+anywheresoftware.b4a.keywords.Common.CRLF+"Download Free at here : http://www.myanmarandroidapp.com/search?q=Myanmar+Radio+App");
 //BA.debugLineNum = 188;BA.debugLine="ShareIt.Initialize (ShareIt.ACTION_SEND,\"\")";
_shareit.Initialize(_shareit.ACTION_SEND,"");
 //BA.debugLineNum = 189;BA.debugLine="ShareIt.SetType (\"text/plain\")";
_shareit.SetType("text/plain");
 //BA.debugLineNum = 190;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.TEXT\",cop";
_shareit.PutExtra("android.intent.extra.TEXT",(Object)(_copy.getText(mostCurrent.activityBA)));
 //BA.debugLineNum = 191;BA.debugLine="ShareIt.PutExtra (\"android.intent.extra.SUBJECT\",";
_shareit.PutExtra("android.intent.extra.SUBJECT",(Object)("Get Free!!"));
 //BA.debugLineNum = 192;BA.debugLine="ShareIt.WrapAsIntentChooser(\"Share App Via...\")";
_shareit.WrapAsIntentChooser("Share App Via...");
 //BA.debugLineNum = 193;BA.debugLine="StartActivity (ShareIt)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_shareit.getObject()));
 //BA.debugLineNum = 194;BA.debugLine="End Sub";
return "";
}
public static String  _ts_click() throws Exception{
 //BA.debugLineNum = 138;BA.debugLine="Sub ts_Click";
 //BA.debugLineNum = 139;BA.debugLine="StartActivity(Thitsar)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._thitsar.getObject()));
 //BA.debugLineNum = 140;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 141;BA.debugLine="End Sub";
return "";
}
public static String  _tyt_click() throws Exception{
 //BA.debugLineNum = 143;BA.debugLine="Sub tyt_Click";
 //BA.debugLineNum = 144;BA.debugLine="StartActivity(TineyinThar)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._tineyinthar.getObject()));
 //BA.debugLineNum = 145;BA.debugLine="ad.Enabled = True";
_ad.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 146;BA.debugLine="End Sub";
return "";
}
}
