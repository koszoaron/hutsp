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

public class NearestInsertionTsp extends AbstractTsp {

    public NearestInsertionTsp(List<City> selectedCities) {
        super(selectedCities);
        logTag = NearestInsertionTsp.class.getSimpleName();
    }

    @Override
    public List<City> doTsp() {
        List<City> res = new ArrayList<City>();
        Iterator<City> iter = selectedCities.iterator();
        if (iter.hasNext()) {
            City node = iter.next();
            int insertion = 0;
            while (!selectedCities.isEmpty()) {
                res.add(insertion, node);
                if (selectedCities.remove(node) && selectedCities.isEmpty()) {
                    break;
                }
                City nearest = null;
                insertion = -1;
                double min = Double.POSITIVE_INFINITY;
                City city0 = res.get(res.size()-1);
                for (int c1 = 0; c1 < res.size(); c1++) {
                    City city1 = res.get(c1);
                    for (City c : selectedCities) {
                        double dist = getDistance(city1, c);
                        if (min > dist) {
                            min = dist;
                            int c2 = (c1 + 1) % res.size();
                            if (getDistance(city0, c) > getDistance(res.get(c2), c)) {
                                insertion = c2;
                            } else {
                                insertion = c1;
                            }
                            nearest = c;
                        }
                    }
                    city0 = city1;
                }
                if (insertion != -1 && nearest != null) {
                    node = nearest;
                }
            }
        }
        
        return res;
    }

}
