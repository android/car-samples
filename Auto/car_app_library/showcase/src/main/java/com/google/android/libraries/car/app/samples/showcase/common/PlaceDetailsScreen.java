/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.libraries.car.app.samples.showcase.common;

import static com.google.android.libraries.car.app.model.Action.BACK;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.Pane;
import com.google.android.libraries.car.app.model.PaneTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import java.util.ArrayList;
import java.util.List;

/** A screen that displays the details of a place. */
public class PlaceDetailsScreen extends Screen {
  private final PlaceInfo mPlace;

  public static PlaceDetailsScreen create(CarContext carContext, PlaceInfo place) {
    return new PlaceDetailsScreen(carContext, place);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    List<Action> actions = new ArrayList<>();
    actions.add(
        Action.builder()
            .setTitle("Navigate")
            .setBackgroundColor(CarColor.BLUE)
            .setOnClickListener(this::onClickNavigate)
            .build());
    actions.add(
        Action.builder()
            .setTitle("Dial")
            .setOnClickListener(this::onClickDial)
            .build());

    Pane.Builder paneBuilder =
        Pane.builder()
            .setActions(actions)
            .addRow(Row.builder().setTitle("Address").addText(mPlace.address).build())
            .addRow(Row.builder().setTitle("Phone").addText(mPlace.phoneNumber).build());

    return PaneTemplate.builder(paneBuilder.build())
        .setTitle(mPlace.title)
        .setHeaderAction(BACK)
        .build();
  }

  private void onClickNavigate() {
    Uri uri = Uri.parse("geo:0,0?q=" + mPlace.address);
    Intent intent = new Intent(CarContext.ACTION_NAVIGATE, uri);
    getCarContext().startCarApp(intent);
  }

  private void onClickDial() {
    Uri uri = Uri.parse("tel:" + mPlace.phoneNumber);
    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
    getCarContext().startCarApp(intent);
  }

  private PlaceDetailsScreen(CarContext carContext, PlaceInfo place) {
    super(carContext);
    mPlace = place;
  }
}
