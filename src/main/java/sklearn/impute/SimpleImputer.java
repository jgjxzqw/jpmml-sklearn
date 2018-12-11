/*
 * Copyright (c) 2015 Villu Ruusmann
 *
 * This file is part of JPMML-SkLearn
 *
 * JPMML-SkLearn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-SkLearn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-SkLearn.  If not, see <http://www.gnu.org/licenses/>.
 */
package sklearn.impute;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.MissingValueTreatmentMethod;
import org.jpmml.converter.Feature;
import org.jpmml.sklearn.ClassDictUtil;
import org.jpmml.sklearn.SkLearnEncoder;
import sklearn.HasNumberOfFeatures;
import sklearn.Transformer;

public class SimpleImputer extends Transformer implements HasNumberOfFeatures {

	public SimpleImputer(String module, String name){
		super(module, name);
	}

	@Override
	public int getNumberOfFeatures(){
		int[] shape = getStatisticsShape();

		return shape[0];
	}

	@Override
	public List<Feature> encodeFeatures(List<Feature> features, SkLearnEncoder encoder){
		Object missingValues = getMissingValues();
		List<?> statistics = getStatistics();
		String strategy = getStrategy();

		ClassDictUtil.checkSize(features, statistics);

		MissingValueTreatmentMethod missingValueTreatment = parseStrategy(strategy);

		List<Feature> result = new ArrayList<>();

		for(int i = 0; i < features.size(); i++){
			Feature feature = features.get(i);
			Object statistic = statistics.get(i);

			result.add(ImputerUtil.encodeFeature(feature, missingValues, statistic, missingValueTreatment, encoder));
		}

		return result;
	}

	public Object getMissingValues(){
		return get("missing_values");
	}

	public List<?> getStatistics(){
		return getArray("statistics_");
	}

	public int[] getStatisticsShape(){
		return getArrayShape("statistics_", 1);
	}

	public String getStrategy(){
		return (String)get("strategy");
	}

	static
	private MissingValueTreatmentMethod parseStrategy(String strategy){

		switch(strategy){
			case "constant":
				return MissingValueTreatmentMethod.AS_VALUE;
			case "mean":
				return MissingValueTreatmentMethod.AS_MEAN;
			case "median":
				return MissingValueTreatmentMethod.AS_MEDIAN;
			case "most_frequent":
				return MissingValueTreatmentMethod.AS_MODE;
			default:
				throw new IllegalArgumentException(strategy);
		}
	}
}
