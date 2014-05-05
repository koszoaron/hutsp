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
import java.util.Iterator;
import java.util.List;

public class NearestNeighbourTsp extends AbstractTsp {

    public NearestNeighbourTsp(List<City> selectedCities) {
        super(selectedCities);
        logTag = NearestNeighbourTsp.class.getSimpleName();
    }

    @Override
    public List<City> doTsp() {
        List<City> res = new ArrayList<City>();
        Iterator<City> iter = selectedCities.iterator();
        if (iter.hasNext()) {
            City node = iter.next();
            while (!selectedCities.isEmpty()) {
                res.add(node);
                if (selectedCities.remove(node) && selectedCities.isEmpty()) {
                    break;
                }
                
                City temp = null;
                double min = Double.POSITIVE_INFINITY;
                for (City c : selectedCities) {
                    double dist = getDistance(c, node);
                    if (min > dist) {
                        min = dist;
                        temp = c;
                    }
                }
                if (temp != null) {
                    node = temp;
                } else {
                    break;
                }
            }
        }
        
        return res;
    }

}
