/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drivona.speed.utils.map

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.google.android.libraries.navigation.Navigator
import com.drivona.speed.R

/** Starts and stops the forwarding of turn-by-turn nav info from Nav SDK. */
object NavForwardingManager {
  /**
   * Registers a service to receive navigation updates and creates a fragment to display the
   * received nav info.
   */
  fun startNavForwarding(
    navigator: Navigator,
    containerViewId:Int,
    context: AppCompatActivity,
    fragmentManager: FragmentManager,
  ): Fragment? {
    val success =
      navigator.registerServiceForNavUpdates(
        context.packageName,
        NavInfoReceivingService::class.java.name,
        Int.MAX_VALUE,
      ) // Send all remaining steps.

    val activity = context
    if(activity == null || activity.isFinishing || activity.isDestroyed){
      // 页面已销毁，直接放弃执行，不提交事务
      return null
    }
    // 2. 关键：判断生命周期，必须至少STARTED，避免onSaveInstanceState之后提交事务
    if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
      return null
    }
    // 3. fragmentManager判空保护
    if (fragmentManager.isDestroyed) {
      return null
    }
    val navInfoDisplayFragment: Fragment = NavInfoDisplayFragment()
    fragmentManager.beginTransaction().add(containerViewId, navInfoDisplayFragment).commit()
    return navInfoDisplayFragment
  }

  fun startNavBottomForwarding(
    navigator: Navigator,
    containerViewId:Int,
    context: Context,
    fragmentManager: FragmentManager,
  ): NavInfoBottomDisplayFragment {
    val success =
      navigator.registerServiceForNavUpdates(
        context.packageName,
        NavInfoReceivingService::class.java.name,
        Int.MAX_VALUE,
      ) // Send all remaining steps.

    val navInfoDisplayFragment = NavInfoBottomDisplayFragment()
    fragmentManager.beginTransaction().add(containerViewId, navInfoDisplayFragment).commit()
    return navInfoDisplayFragment
  }

  /**
   * Unregisters the service receiving navigation updates and removes the nav info display fragment.
   */
  fun stopNavForwarding(
    navigator: Navigator,
    context: Context,
    fragmentManager: FragmentManager,
    navInfoFragment: Fragment,
  ) {
    // Remove the display header.
    fragmentManager.beginTransaction().remove(navInfoFragment).commit()
    // Unregister the nav info receiving service.
    val success = navigator.unregisterServiceForNavUpdates()
    if (success) {
      Toast.makeText(context, "Unregistered service for nav updates", Toast.LENGTH_SHORT).show()
    } else {
      // This may happen if no service had been registered.
      Toast.makeText(context, "Failed to unregister service for nav updates", Toast.LENGTH_SHORT)
        .show()
    }
  }
}
