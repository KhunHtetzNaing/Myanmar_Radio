Type=Activity
Version=6.8
ModulesStructureVersion=1
B4A=true
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Dim wv As WebView
	Dim N As NativeExpressAd
	Dim B As AdView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.Title = "Tine Yin Thar"
	Dim h As String
	h = File.ReadString(File.DirAssets,"Tineyinthar")
	wv.Initialize("wv")
	'	wv.LoadHtml(h)
	wv.LoadHtml(h)
	Activity.AddView(wv,0%x,0%y,100%x,100%y)
	B.Initialize2("B","ca-app-pub-4173348573252986/4119293753",B.SIZE_SMART_BANNER)
	Dim height As Int
	If GetDeviceLayoutValues.ApproximateScreenSize < 6 Then
		If 100%x > 100%y Then height = 32dip Else height = 50dip
	Else
		height = 90dip
	End If
	Activity.AddView(B, 0dip, 100%y - height, 100%x, height)
	B.LoadAd
	Log(B)
	
	N.Initialize("N","ca-app-pub-4173348573252986/9828933353",100%x,132dip)
	N.LoadAd
	Activity.AddView(N,0%x,60%y,100%x,132dip)
	
	Activity.AddMenuItem3("Share","share",LoadBitmap(File.DirAssets,"share.png"),True)
End Sub

Sub wv_OverrideUrl(Url As String) As Boolean
	Log(Url)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



Sub N_FailedToReceiveAd (ErrorCode As String)
	Log("N failed: " & ErrorCode)
End Sub
Sub N_ReceiveAd
	Log("N received")
End Sub

Sub N_AdScreenDismissed
	Log("N Dismissed")
End Sub

Sub B_FailedToReceiveAd (ErrorCode As String)
	Log("B failed: " & ErrorCode)
End Sub

Sub B_ReceiveAd
	Log("B received")
End Sub

Sub B_AdScreenDismissed
	Log("B Dismissed")
End Sub
Sub share_Click
	Dim ShareIt As Intent
	Dim copy As BClipboard
	copy.clrText
	copy.setText("မိမိတို႔ဖုန္းကေနတဆင့္ ျမန္မာေရဒီယိုလိုင္းမ်ား 'အခမဲ့' နားဆင္နိုင္တဲ့ Myanmar Radio App ေလးပါ။"&CRLF& "ပါဝင္ေသာ ေရဒီယိုလိုင္းမ်ားမွာ..." &CRLF& "ခ်ယ္ရီ FM" &CRLF& "မႏၲေလး FM" &CRLF& "ေရႊ FM" &CRLF& "ပတၱျမား FM" &CRLF& "MRTV" &CRLF& "သစၥာပါရမီ" &CRLF& "တိုင္းရင္းသား" &CRLF& "Download Free at here : http://www.myanmarandroidapp.com/search?q=Myanmar+Radio+App")
	ShareIt.Initialize (ShareIt.ACTION_SEND,"")
	ShareIt.SetType ("text/plain")
	ShareIt.PutExtra ("android.intent.extra.TEXT",copy.getText)
	ShareIt.PutExtra ("android.intent.extra.SUBJECT","Get Free!!")
	ShareIt.WrapAsIntentChooser("Share App Via...")
	StartActivity (ShareIt)
End Sub