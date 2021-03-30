package dev.idm.vkp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.Arrays;

import dev.idm.vkp.idmapi.IdmApiService;
import dev.idm.vkp.idmapi.models.VerifiedResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CheckDonate {
    public static Integer[] donatedUsers = {};
    public static Integer[] owners = {};
    public static Integer[] agents = {};
    public static Integer[] helpers = {};
    public static Integer[] donuts = {};

    public static Integer[] getDonatedUsers() {
        return ArrayUtils.concat(CheckDonate.owners, CheckDonate.agents, CheckDonate.helpers, CheckDonate.donuts);
    }


    @SuppressLint("CheckResult")
    public static void updateDonatedUsers() {
        IdmApiService.Factory.create()
                .getVerified()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> {
                            VerifiedResponse response = result.getResponse();
                            if (response == null) {
                                Log.e("CheckDonate", "Parsing error");
                                return;
                            }
                            int[] _owners = response.getOwner();
                            int[] _agents = response.getAgents();
                            int[] _helpers = response.getHelpers();
                            int[] _donuts = response.getDonuts();

                            if (_agents == null || _owners == null || _helpers == null || _donuts == null) {
                                Log.e("CheckDonate", "Parsing error");
                                return;
                            }
                            CheckDonate.owners = Arrays.stream(_owners).boxed().toArray( Integer[]::new );
                            CheckDonate.agents = Arrays.stream(_agents).boxed().toArray( Integer[]::new );
                            CheckDonate.helpers = Arrays.stream(_helpers).boxed().toArray( Integer[]::new );
                            CheckDonate.donuts = Arrays.stream(_donuts).boxed().toArray( Integer[]::new );
                            Log.d(
                                    "CheckDonate",
                                    "Owners " + Arrays.toString(CheckDonate.owners) + "\n" +
                                            "Agents " + Arrays.toString(CheckDonate.agents) + "\n" +
                                            "Helpers " + Arrays.toString(CheckDonate.agents) + "\n" +
                                            "Donuts " + Arrays.toString(CheckDonate.donuts)
                                    );
                        },
                        error -> {
                            Log.e("CheckDonate", error.getMessage(), error);
                        }
                );
    }

    public static boolean isFullVersion(@NonNull Context context) {
        return true;
    }
}
