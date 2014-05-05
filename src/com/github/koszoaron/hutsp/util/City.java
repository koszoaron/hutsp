/**
 *  Copyright (C) 2014  Aron Koszo
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.koszoaron.hutsp.util;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class City implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private double latitude;
    private double longitude;
    
    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public String getName() {
        return name;
    }
    
    public double getLat() {
        return latitude;
    }
    
    public double getLong() {
        return longitude;
    }
    
    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof City) {
            City c = (City)o;
            if (c.getName().equalsIgnoreCase(name) && c.getLat()==latitude && c.getLong()==longitude) {
                return true;
            }
        }
        return false;
    }
}
