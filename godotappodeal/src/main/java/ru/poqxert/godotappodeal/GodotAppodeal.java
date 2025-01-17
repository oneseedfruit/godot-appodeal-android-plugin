package ru.poqxert.godotappodeal;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.UserSettings;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.appodeal.ads.utils.Log;
import com.appodeal.consent.Consent;
import com.appodeal.consent.ConsentForm;
import com.appodeal.consent.ConsentFormListener;
import com.appodeal.consent.ConsentManagerError;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GodotAppodeal extends GodotPlugin {
    private final Activity activity;

    private FrameLayout layout = null;

    public GodotAppodeal(Godot godot) {
        super(godot);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onMainCreate(Activity activity) {
        layout = new FrameLayout(activity);
        return layout;
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotAppodeal";
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signalInfoSet = new HashSet<>();

        // Consent form
        signalInfoSet.add(new SignalInfo("consent_form_loaded"));
        signalInfoSet.add(new SignalInfo("consent_form_shown"));
        signalInfoSet.add(new SignalInfo("consent_form_load_failed", String.class));
        signalInfoSet.add(new SignalInfo("consent_form_closed"));
        // Interstitial
        signalInfoSet.add(new SignalInfo("interstitial_loaded", Boolean.class));
        signalInfoSet.add(new SignalInfo("interstitial_load_failed"));
        signalInfoSet.add(new SignalInfo("interstitial_show_failed"));
        signalInfoSet.add(new SignalInfo("interstitial_shown"));
        signalInfoSet.add(new SignalInfo("interstitial_closed"));
        signalInfoSet.add(new SignalInfo("interstitial_clicked"));
        signalInfoSet.add(new SignalInfo("interstitial_expired"));
        // Banner
        signalInfoSet.add(new SignalInfo("banner_loaded", Boolean.class));
        signalInfoSet.add(new SignalInfo("banner_load_failed"));
        signalInfoSet.add(new SignalInfo("banner_shown"));
        signalInfoSet.add(new SignalInfo("banner_clicked"));
        signalInfoSet.add(new SignalInfo("banner_expired"));
        // Rewarded video
        signalInfoSet.add(new SignalInfo("rewarded_video_loaded", Boolean.class));
        signalInfoSet.add(new SignalInfo("rewarded_video_load_failed"));
        signalInfoSet.add(new SignalInfo("rewarded_video_shown"));
        signalInfoSet.add(new SignalInfo("rewarded_video_show_failed"));
        signalInfoSet.add(new SignalInfo("rewarded_video_clicked"));
        signalInfoSet.add(new SignalInfo("rewarded_video_finished", Double.class, String.class));
        signalInfoSet.add(new SignalInfo("rewarded_video_closed", Boolean.class));
        signalInfoSet.add(new SignalInfo("rewarded_video_expired"));

        return  signalInfoSet;
    }

    private int getAdType(int value) {
        int res = Appodeal.NONE;
        if((value&1) != 0) {
            res |= Appodeal.INTERSTITIAL;
        }
        if((value&2) != 0) {
            res |= Appodeal.BANNER;
        }
        if((value&4) != 0) {
            res |= Appodeal.NATIVE;
        }
        if((value&8) != 0) {
            res |= Appodeal.REWARDED_VIDEO;
        }
        return res;
    }

    private int getShowStyle(int value) {
        int res = Appodeal.NONE;
        if((value&1) != 0) {
            res |= Appodeal.INTERSTITIAL;
        }
        if((value&2) != 0) {
            res |= Appodeal.BANNER_TOP;
        }
        if((value&4) != 0) {
            res |= Appodeal.BANNER_BOTTOM;
        }
        if((value&8) != 0) {
            res |= Appodeal.REWARDED_VIDEO;
        }
        return res;
    }

    private void setCallbacks(int types) {
        if((types&Appodeal.INTERSTITIAL) != 0) {
            Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
                @Override
                public void onInterstitialLoaded(boolean b) {
                    emitSignal("interstitial_loaded", b);
                }

                @Override
                public void onInterstitialFailedToLoad() {
                    emitSignal("interstitial_load_failed");
                }

                @Override
                public void onInterstitialShown() {
                    emitSignal("interstitial_shown");
                }

                @Override
                public void onInterstitialShowFailed() {
                    emitSignal("interstitial_show_failed");
                }

                @Override
                public void onInterstitialClicked() {
                    emitSignal("interstitial_clicked");
                }

                @Override
                public void onInterstitialClosed() {
                    emitSignal("interstitial_closed");
                }

                @Override
                public void onInterstitialExpired() {
                    emitSignal("interstitial_expired");
                }
            });
        }
        if((types&Appodeal.BANNER) != 0) {
            Appodeal.setBannerCallbacks(new BannerCallbacks() {
                @Override
                public void onBannerLoaded(int i, boolean b) {
                    emitSignal("banner_loaded", b);
                }

                @Override
                public void onBannerFailedToLoad() {
                    emitSignal("banner_load_failed");
                }

                @Override
                public void onBannerShown() {
                    emitSignal("banner_shown");
                }

                @Override
                public void onBannerShowFailed() {
                    emitSignal("banner_show_failed");
                }

                @Override
                public void onBannerClicked() {
                    emitSignal("banner_clicked");
                }

                @Override
                public void onBannerExpired() {
                    emitSignal("banner_expired");
                }
            });
        }
        if((types&Appodeal.REWARDED_VIDEO) != 0) {
            Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
                @Override
                public void onRewardedVideoLoaded(boolean b) {
                    emitSignal("rewarded_video_loaded", b);
                }

                @Override
                public void onRewardedVideoFailedToLoad() {
                    emitSignal("rewarded_video_load_failed");
                }

                @Override
                public void onRewardedVideoShown() {
                    emitSignal("rewarded_video_shown");
                }

                @Override
                public void onRewardedVideoShowFailed() {
                    emitSignal("rewarded_video_show_failed");
                }

                @Override
                public void onRewardedVideoFinished(double v, String s) {
                    if (s == null) s = "";
                    emitSignal("rewarded_video_finished", v, s);
                }

                @Override
                public void onRewardedVideoClosed(boolean b) {
                    emitSignal("rewarded_video_closed", b);
                }

                @Override
                public void onRewardedVideoExpired() {
                    emitSignal("rewarded_video_expired");
                }

                @Override
                public void onRewardedVideoClicked() {
                    emitSignal("rewarded_video_clicked");
                }
            });
        }
    }

    @UsedByGodot
    public void setTestingEnabled(boolean testing) {
        Appodeal.setTesting(testing);
    }

    @UsedByGodot
    public void disableNetworks(String[] networks) {
        int len = networks.length;
        for(int i = 0; i < len; i++) {
            disableNetwork(networks[i]);
        }
    }

    @UsedByGodot
    public void disableNetworksForAdType(String[] networks, int adType) {
        int len = networks.length;
        for(int i = 0; i < len; i++) {
            disableNetworkForAdType(networks[i], adType);
        }
    }

    @UsedByGodot
    public void disableNetwork(String network) {
        Appodeal.disableNetwork(network);
    }

    @UsedByGodot
    public void disableNetworkForAdType(String network, int adType) {
        Appodeal.disableNetwork(network, getAdType(adType));
    }

    @UsedByGodot
    public double getPredictedEcpmForAdType(int adType) {
        return Appodeal.getPredictedEcpm(getAdType(adType));
    }

    @UsedByGodot
    public void setAutocache(boolean enabled, int adType) {
        Appodeal.setAutoCache(getAdType(adType), enabled);
    }

    @UsedByGodot
    public boolean isAutocacheEnabled(int adType) {
        return Appodeal.isAutoCacheEnabled(getAdType(adType));
    }

    @UsedByGodot
    public void initialize(String appKey, int adTypes) {
        int types = getAdType(adTypes);
        setCallbacks(types);
        Appodeal.initialize(activity, appKey, types, new ApdInitializationCallback() {
            @Override
            public void onInitializationFinished(List<ApdInitializationError> list) {

            }
        });
    }

    @UsedByGodot
    public boolean isInitializedForAdType(int adType) {
        return Appodeal.isInitialized(getAdType(adType));
    }

    @UsedByGodot
    public void setLogLevel(int level) {
        Appodeal.setLogLevel(Log.LogLevel.fromInteger(level));
    }

    @UsedByGodot
    public void setExtras(Dictionary extras) {
        String[] keys = extras.get_keys();
        int len = keys.length;
        for(int i = 0; i < len; i++) {
            String key = keys[i];
            Object val = extras.get(key);
            if(val instanceof Integer) {
                Appodeal.setExtraData(key, (int)val);
            } else if(val instanceof Double) {
                Appodeal.setExtraData(key, (double)val);
            } else if(val instanceof Boolean) {
                Appodeal.setExtraData(key, (boolean)val);
            } else if(val instanceof String) {
                Appodeal.setExtraData(key, (String)val);
            }
        }
    }

    @UsedByGodot
    public void setChildDirectedTreatment(boolean value) {
        Appodeal.setChildDirectedTreatment(value);
    }

    @UsedByGodot
    public void showConsentForm() {
        ConsentFormListener consentFormListener = new ConsentFormListener() {
            @Override
            public void onConsentFormLoaded(ConsentForm consentForm) {
                emitSignal("consent_form_loaded");
                consentForm.show();
            }

            @Override
            public void onConsentFormError(ConsentManagerError error) {
                // Consent form loading or showing failed. More info can be found in 'error' object
                // Initialize the Appodeal SDK with default params.
                String consentError = "[Appodeal] " + error.getEvent() + ": " + error.getMessage();
                emitSignal("consent_form_load_failed", consentError);
            }

            @Override
            public void onConsentFormOpened() {
                // Consent form was shown
                emitSignal("consent_form_shown");
            }

            @Override
            public void onConsentFormClosed(Consent consent) {
                // Consent form was closed, you may initialize Appodeal SDK here
                Appodeal.updateConsent(consent);
                emitSignal("consent_form_closed");
            }
        };

        // Create new Consent form instance
        ConsentForm consentForm = new ConsentForm(activity.getApplicationContext(), consentFormListener);
        consentForm.load();
    }

    @UsedByGodot
    public void setUserId(String userId) {
        Appodeal.setUserId(userId);
    }

    @UsedByGodot
    public boolean canShow(int style) {
        return Appodeal.canShow(getShowStyle(style));
    }

    @UsedByGodot
    public boolean canShowForPlacement(int adType, String placementName) {
        return Appodeal.canShow(adType, placementName);
    }

    @UsedByGodot
    public boolean showAd(int style) {
        boolean can = canShow(style);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Appodeal.show(activity, getShowStyle(style));
            }
        });
        return can;
    }

    @UsedByGodot
    public boolean showAdForPlacement(int style, String placementName) {
        boolean can = canShowForPlacement(getShowStyle(style), placementName);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Appodeal.show(activity, getShowStyle(style), placementName);
            }
        });
        return can;
    }

    @UsedByGodot
    public void cacheAd(int adType) {
        Appodeal.cache(activity, getAdType(adType));
    }

    @UsedByGodot
    public boolean isPrecacheAd(int adType) {
        return Appodeal.isPrecache(getAdType(adType));
    }

    @UsedByGodot
    public void setSegmentFilter(Dictionary filter) {
        String[] keys = filter.get_keys();
        int len = keys.length;
        for(int i = 0; i < len; i++) {
            String key = keys[i];
            Object val = filter.get(key);
            if(val instanceof Integer) {
                Appodeal.setCustomFilter(key, (int)val);
            } else if(val instanceof Double) {
                Appodeal.setCustomFilter(key, (double)val);
            } else if(val instanceof Boolean) {
                Appodeal.setCustomFilter(key, (boolean)val);
            } else if(val instanceof String) {
                Appodeal.setCustomFilter(key, (String)val);
            }
        }
    }

    @UsedByGodot
    public void setPreferredBannerAdSize(int size) {
        Appodeal.set728x90Banners(size == 1);
    }

    @UsedByGodot
    public void hideBanner() {
        Appodeal.hide(activity, Appodeal.BANNER);
    }

    @UsedByGodot
    public void setSmartBannersEnabled(boolean enabled) {
        Appodeal.setSmartBanners(enabled);
    }

    @UsedByGodot
    public void setBannerAnimationEnabled(boolean enabled) {Appodeal.setBannerAnimation(enabled);}

    @UsedByGodot
    public Dictionary getRewardForPlacement(String placement) {
        Pair<Double, String> reward = Appodeal.getRewardParameters(placement);
        Dictionary res = new Dictionary();
        res.put("currency", reward.second);
        res.put("amount", reward.first);
        return res;
    }

    @UsedByGodot
    public void muteVideosIfCallsMuted(boolean mute) {
        Appodeal.muteVideosIfCallsMuted(mute);
    }

    @UsedByGodot
    public void trackInAppPurchase(double amount, String currencyCode) {
        Appodeal.trackInAppPurchase(activity.getApplicationContext(), amount, currencyCode);
    }
}
