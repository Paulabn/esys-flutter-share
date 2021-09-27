package de.esys.esysfluttershare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * EsysFlutterSharePlugin
 */
public class EsysFlutterSharePlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

    private final String PROVIDER_AUTH_EXT = ".fileprovider.github.com/orgs/esysberlin/esys-flutter-share";
    private Registrar _registrar;
    private FlutterPluginBinding _binding;
    private Activity _activity;

    public EsysFlutterSharePlugin() {
    }

    private EsysFlutterSharePlugin(Registrar registrar) {
        this._registrar = registrar;
    }

    private EsysFlutterSharePlugin(FlutterPluginBinding binding) {
        this._binding = binding;
    }

    /**
     * Plugin registration.
     */
    public  void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "channel:github.com/orgs/esysberlin/esys-flutter-share");
        this._registrar = registrar;
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("text")) {
            text(call.arguments);
        }
        if (call.method.equals("file")) {
            file(call.arguments);
        }
        if (call.method.equals("files")) {
            files(call.arguments);
        }
    }

    private void text(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String text = argsMap.get("text");
        String mimeType = argsMap.get("mimeType");

        Context activeContext = null;

        if (_registrar != null) {
            activeContext = _registrar.activeContext();
        } else if (_binding != null){
            activeContext = _binding.getApplicationContext();
        }


            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(mimeType);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        if (_activity != null) {
            _activity.startActivity(Intent.createChooser(shareIntent, title));
        } else if (activeContext != null) {
            activeContext.startActivity(Intent.createChooser(shareIntent, title));
        }
    }

    private void file(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, String> argsMap = (HashMap<String, String>) arguments;
        String title = argsMap.get("title");
        String name = argsMap.get("name");
        String mimeType = argsMap.get("mimeType");
        String text = argsMap.get("text");


        Context activeContext = null;

        if (_registrar != null) {
            activeContext = _registrar.activeContext();
        } else if (_binding != null){
            activeContext = _binding.getApplicationContext();
        }


            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(mimeType);
            File file = new File(activeContext.getCacheDir(), name);
            String fileProviderAuthority = activeContext.getPackageName() + PROVIDER_AUTH_EXT;
            Uri contentUri = FileProvider.getUriForFile(activeContext, fileProviderAuthority, file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            // add optional text
            if (!text.isEmpty()) shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        if (_activity != null){
            _activity.startActivity(Intent.createChooser(shareIntent, title));
        }  else if (activeContext != null) {
            activeContext.startActivity(Intent.createChooser(shareIntent, title));
        }
    }

    private void files(Object arguments) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> argsMap = (HashMap<String, Object>) arguments;
        String title = (String) argsMap.get("title");

        @SuppressWarnings("unchecked")
        ArrayList<String> names = (ArrayList<String>) argsMap.get("names");
        String mimeType = (String) argsMap.get("mimeType");
        String text = (String) argsMap.get("text");

        Context activeContext = null;

        if (_registrar != null) {
            activeContext = _registrar.activeContext();
        } else if (_binding != null){
            activeContext = _binding.getApplicationContext();
        }


            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType(mimeType);

            ArrayList<Uri> contentUris = new ArrayList<>();

            for (String name : names) {
                File file = new File(activeContext.getCacheDir(), name);
                String fileProviderAuthority = activeContext.getPackageName() + PROVIDER_AUTH_EXT;
                contentUris.add(FileProvider.getUriForFile(activeContext, fileProviderAuthority, file));
            }

            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris);
            // add optional text
            if (!text.isEmpty()) shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            if (_activity != null) {
                _activity.startActivity(Intent.createChooser(shareIntent, title));
            } else if (activeContext != null){
                activeContext.startActivity(Intent.createChooser(shareIntent, title));
            }
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "channel:github.com/orgs/esysberlin/esys-flutter-share");
        this._binding = flutterPluginBinding;
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        _activity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        _activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        _activity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        _activity = null;
    }
}
