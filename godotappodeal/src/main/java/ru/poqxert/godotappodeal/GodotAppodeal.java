package ru.poqxert.godotappodeal;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.NonSkippableVideoCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.UserSettings;
import com.appodeal.ads.api.App;
import com.appodeal.ads.utils.Log;
import com.appodeal.ads.utils.PermissionsHelper;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.security.Permissions;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GodotAppodeal extends GodotPlugin {
    private Activity activity;

    private FrameLayout layout = null;

    public GodotAppodeal(Godot godot) {
        super(godot);
        activity = godot;
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
    public List<String> getPluginMethods() {
        return Arrays.asList(
                "setTestingEnabled",
                "disableNetworks",
                "disableNetworksForAdType",
                "disableNetwork",
                "disableNetworkForAdType",
                "getPredictedEcpmForAdType",
                "requestAndroidMPermissions",
                "setLocationTracking",
                "setAutocache",
                "isAutocacheEnabled",
                "initialize",
                "isInitializedForAdType",
                "setLogLevel",
                "setExtras",
                "setChildDirectedTreatment",
                "updateConsent",
                "setUserId",
                "setUserAge",
                "setUserGender",
                "canShow",
                "canShowForPlacement",
                "showAd",
                "showAdForPlacement",
                "cacheAd",
                "isPrecacheAd",
                "setSegmentFilter",
                "setPreferredBannerAdSize",
                "hideBanner",
                "setSmartBannersEnabled",
                "setBannerAnimationEnabled",
                "getRewardForPlacement",
                "trackInAppPurchase",
                "disableWriteExternalStoragePermissionCheck",
                "muteVideosIfCallsMuted"
                );
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signalInfoSet = new HashSet<>();
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
        // Non-Skippable video
        signalInfoSet.add(new SignalInfo("non_skippable_video_loaded", Boolean.class));
        signalInfoSet.add(new SignalInfo("non_skippable_video_load_failed"));
        signalInfoSet.add(new SignalInfo("non_skippable_video_shown"));
        signalInfoSet.add(new SignalInfo("non_skippable_video_show_failed"));
        signalInfoSet.add(new SignalInfo("non_skippable_video_clicked"));
        signalInfoSet.add(new SignalInfo("non_skippable_video_closed", Boolean.class));
        signalInfoSet.add(new SignalInfo("non_skippable_video_expired"));
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
        } else if((value&16) != 0) {
            res |= Appodeal.NON_SKIPPABLE_VIDEO;
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
        } else if((value&16) != 0) {
            res |= Appodeal.NON_SKIPPABLE_VIDEO;
        }
        return res;
    }

    public void setTestingEnabled(boolean testing) {
        Appodeal.setTesting(testing);
    }

    public void disableNetworks(String[] networks) {
        int len = networks.length;
        for(int i = 0; i < len; i++) {
            disableNetwork(networks[i]);
        }
    }

    public void disableNetworksForAdType(String[] networks, int adType) {
        int len = networks.length;
        for(int i = 0; i < len; i++) {
            disableNetworkForAdType(networks[i], adType);
        }
    }

    public void disableNetwork(String network) {
        Appodeal.disableNetwork(activity, network);
    }

    public void disableNetworkForAdType(String network, int adType) {
        Appodeal.disableNetwork(activity, network, getAdType(adType));
    }

    public double getPredictedEcpmForAdType(int adType) {
        return Appodeal.getPredictedEcpm(getAdType(adType));
    }

    public void requestAndroidMPermissions() {
        Appodeal.requestAndroidMPermissions(activity, new PermissionsHelper.AppodealPermissionCallbacks() {
            @Override
            public void writeExternalStorageResponse(int i) {
            }

            @Override
            public void accessCoarseLocationResponse(int i) {

            }
        });
    }

    public void setLocationTracking(boolean enabled) {
        if(!enabled) {
            Appodeal.disableLocationPermissionCheck();
        }
    }

    public void setAutocache(boolean enabled, int adType) {
        Appodeal.setAutoCache(getAdType(adType), enabled);
    }

    public boolean isAutocacheEnabled(int adType) {
        return Appodeal.isAutoCacheEnabled(getAdType(adType));
    }

    public void initialize(String appId, int adTypes, boolean consent) {
        int types = getAdType(adTypes);
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
        if((types&Appodeal.NON_SKIPPABLE_VIDEO) != 0) {
            Appodeal.setNonSkippableVideoCallbacks(new NonSkippableVideoCallbacks() {
                @Override
                public void onNonSkippableVideoLoaded(boolean b) {
                    emitSignal("non_skippable_video_loaded", b);
                }

                @Override
                public void onNonSkippableVideoFailedToLoad() {
                    emitSignal("non_skippable_video_load_failed");
                }

                @Override
                public void onNonSkippableVideoShown() {
                    emitSignal("non_skippable_video_shown");
                }

                @Override
                public void onNonSkippableVideoShowFailed() {
                    emitSignal("non_skippable_video_show_failed");
                }

                @Override
                public void onNonSkippableVideoFinished() {
                    emitSignal("non_skippable_video_finished");
                }

                @Override
                public void onNonSkippableVideoClosed(boolean b) {
                    emitSignal("non_skippable_video_closed", b);
                }

                @Override
                public void onNonSkippableVideoExpired() {
                    emitSignal("non_skippable_video_expired");
                }
            });
        }
        Appodeal.initialize(activity, appId, types, consent);
    }

    public boolean isInitializedForAdType(int adType) {
        return Appodeal.isInitialized(getAdType(adType));
    }

    public void setLogLevel(int level) {
        Appodeal.setLogLevel(Log.LogLevel.fromInteger(level));
    }

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

    public void setChildDirectedTreatment(boolean value) {
        Appodeal.setChildDirectedTreatment(value);
    }

    public void updateConsent(boolean consent) {
        Appodeal.updateConsent(consent);
    }

    public void setUserId(String userId) {
        Appodeal.setUserId(userId);
    }

    public void setUserAge(int age) {
        Appodeal.setUserAge(age);
    }

    public void setUserGender(int gender) {
        UserSettings.Gender g = UserSettings.Gender.fromInteger(gender);
        if(g != null) {
            Appodeal.setUserGender(g);
        }
    }

    public boolean canShow(int style) {
        return Appodeal.canShow(getShowStyle(style));
    }

    public boolean canShowForPlacement(int adType, String placementName) {
        return Appodeal.canShow(adType, placementName);
    }

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

    public void cacheAd(int adType) {
        Appodeal.cache(activity, getAdType(adType));
    }

    public boolean isPrecacheAd(int adType) {
        return Appodeal.isPrecache(getAdType(adType));
    }

    public void setSegmentFilter(Dictionary filter) {
        String[] keys = filter.get_keys();
        int len = keys.length;
        for(int i = 0; i < len; i++) {
            String key = keys[i];
            Object val = filter.get(key);
            if(val instanceof Integer) {
                Appodeal.setSegmentFilter(key, (int)val);
            } else if(val instanceof Double) {
                Appodeal.setSegmentFilter(key, (double)val);
            } else if(val instanceof Boolean) {
                Appodeal.setSegmentFilter(key, (boolean)val);
            } else if(val instanceof String) {
                Appodeal.setSegmentFilter(key, (String)val);
            }
        }
    }

    public void setPreferredBannerAdSize(int size) {
        Appodeal.set728x90Banners(size == 1);
    }

    public void hideBanner() {
        Appodeal.hide(activity, Appodeal.BANNER);
    }

    public void setSmartBannersEnabled(boolean enabled) {
        Appodeal.setSmartBanners(enabled);
    }

    public void setBannerAnimationEnabled(boolean enabled) {Appodeal.setBannerAnimation(enabled);}

    public Dictionary getRewardForPlacement(String placement) {
        Pair<Double, String> reward = Appodeal.getRewardParameters(placement);
        Dictionary res = new Dictionary();
        res.put("currency", reward.second);
        res.put("amount", reward.first);
        return res;
    }

    public void disableWriteExternalStoragePermissionCheck() {
        Appodeal.disableWriteExternalStoragePermissionCheck();
    }

    public void muteVideosIfCallsMuted(boolean mute) {
        Appodeal.muteVideosIfCallsMuted(mute);
    }

    public void trackInAppPurchase(double amount, String currencyCode) {
        Appodeal.trackInAppPurchase(activity.getApplicationContext(), amount, currencyCode);
    }
}