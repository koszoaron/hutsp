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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTsp {
    protected String logTag = "";
    protected List<City> selectedCities;
    
    private static final int RADIUS_EARTH_M = 6371;
    
    protected AbstractTsp(List<City> selectedCities) {
        this.selectedCities = new ArrayList<City>();
        this.selectedCities.addAll(selectedCities);
    }
    
    public abstract List<City> doTsp();
    
    public double getDistance(City c1, City c2) { 
        //Haversine-formula
        Double latDist = toRadians(c1.getLat() - c2.getLat());
        Double longDist = toRadians(c1.getLong() - c2.getLong());
        
        Double a = Math.sin(latDist/2) * Math.sin(latDist/2) + Math.cos(toRadians(c1.getLat())) * Math.cos(toRadians(c2.getLat())) * Math.sin(longDist/2) * Math.sin(longDist/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return RADIUS_EARTH_M * c;
    }
    
    private double toRadians(Double degrees) {
        return degrees * Math.PI / 180;
    }
}
