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

public class NearestAdditionTsp extends AbstractTsp {

    public NearestAdditionTsp(List<City> selectedCities) {
        super(selectedCities);
        logTag = NearestAdditionTsp.class.getSimpleName();
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
                for (int i = 0; i < res.size(); i++) {
                    City x = res.get(i);
                    for (City y : selectedCities) {
                        double dist = getDistance(x, y);
                        if (min > dist) {
                            min = dist;
                            insertion = i;
                            nearest = y;
                        }
                    }
                }
                if (insertion != -1 && nearest != null) {
                    node = nearest;
                } else {
                    break;
                }
            }
        }
        
        return res;
    }

}
