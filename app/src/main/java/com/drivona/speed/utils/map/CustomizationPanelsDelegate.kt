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

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraFollowLocationCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.navigation.ForceNightMode
import com.google.android.libraries.navigation.Navigator
import com.google.android.libraries.navigation.OnNavigationUiChangedListener
import com.google.android.libraries.navigation.SupportNavigationFragment


/**
 * Handles the shared setup and behaviors of the customization panels in `NavViewActivity` and
 * `NavFragmentActivity`.
 *
 * Users are initially presented with a simple overlay of toggles that allow them to select which
 * type of runtime behavior to stress. "Nav methods" refer to behaviors on [Navigator]. "View
 * methods" refer to behaviors on [NavigationView] or [SupportNavigationFragment]. And "Map methods"
 * refer to behaviors on [GoogleMap].
 *
 * Clicking one of the toggles will expand a list of corresponding behaviors, presented as a panel
 * of buttons, spinners etc for the user to play with. The user can always hide/show the entire
 * customization UI (toggles and panels) using the drop-down menu.
 *
 * IMPORTANT: Note that the Navigation SDK is a complex product, and so many of the features
 * presented in the panels are interdependent. For example, calling GoogleMap#followMyLocation also
 * (asynchronously) enables the Navigation style of UI (see [NavigationView.isNavigationUiEnabled]).
 *
 * As a result, the control panels will sometimes change without a user interaction, in response to
 * the Navigation SDK changing its internal state. These complexities are explained in in-line
 * comments below, where applicable.
 */
internal object CustomizationPanelsDelegate {
  private const val TAG = "CustomizationPanels"

  /** The location of Melbourne. */
  private val MELBOURNE = LatLng(-37.813, 144.962)

  ///////////////////////////////////////////////////////////////////////////////////////
  // Handlers for "Map methods" control panel interactions (other than spinners).
  //
  // An aside: Looking for more customizations you can do using GoogleMap?
  //
  // Check out our "GoogleMap" sample app to see all the different behaviors
  // supported by the GoogleMap interface, like drawing markers, setting up camera bounds,
  // and more!
  ///////////////////////////////////////////////////////////////////////////////////////
  /** Moves the position of the camera to hover over Melbourne. */
  fun moveCameraToMelbourne(activity: Activity, googleMap: GoogleMap) {
    // Moving the camera always exits follow mode until it's enabled programmatically or via
    // clicking the "Recenter" button.
    googleMap.moveCamera(
      CameraUpdateFactory.newCameraPosition(
        CameraPosition.builder().target(MELBOURNE).zoom(10f).bearing(0f).build()
      )
    )
  }


  ///////////////////////////////////////////////////////////////////////////////////////
  // Handlers for "Nav methods" control panel interactions (other than spinners).
  ///////////////////////////////////////////////////////////////////////////////////////
  /**
   * Toggles navigation forwarding (e.g. for 2-wheeler projection).
   *
   * @return the new NavInfoDisplayFragment state to manage, to pass in upon the next user-click
   */
  fun toggleNavForwarding(
    activity: AppCompatActivity,
    containerViewId:Int,
    navigator: Navigator,
    existingFragment: Fragment?,
  ): Fragment? {

    return if (existingFragment == null) {
      NavForwardingManager.startNavForwarding(navigator, containerViewId,activity, activity.supportFragmentManager)
    } else {
      NavForwardingManager.stopNavForwarding(
        navigator,
        activity,
        activity.supportFragmentManager,
        existingFragment,
      )
      null
    }
  }
  fun toggleNavBottomForwarding(
    activity: AppCompatActivity,
    containerViewId:Int,
    navigator: Navigator,
    existingFragment: NavInfoBottomDisplayFragment?,
  ): NavInfoBottomDisplayFragment? {

    return if (existingFragment == null) {
      NavForwardingManager.startNavBottomForwarding(navigator, containerViewId,activity, activity.supportFragmentManager)
    } else {
      NavForwardingManager.stopNavForwarding(
        navigator,
        activity,
        activity.supportFragmentManager,
        existingFragment,
      )
      null
    }
  }


  /** An item selection listener for use with the camera perspective spinner. */
  private interface OnCameraPerspectiveSelectedListener : AdapterView.OnItemSelectedListener {
    /**
     * Gets the last non-zero position for the Camera Perspective spinner.
     *
     * Note that this will ignore selections of the default (0-positioned) value of the spinner,
     * since that's just a dummy element to explain the spinner's purpose.
     */
    val lastSetNonZeroPosition: Int
  }
}
