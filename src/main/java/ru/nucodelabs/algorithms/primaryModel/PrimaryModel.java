package ru.nucodelabs.algorithms.primaryModel;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrimaryModel {

    private final List<ExperimentalData> experimentalData;
    public PrimaryModel(List<ExperimentalData> experimentalData) {
        this.experimentalData = experimentalData;
    }

    public List<ModelLayer> get3LayersPrimaryModel() {
        if (experimentalData.size() <= 3) {
           return new ArrayList<>();
        }

        List<ExperimentalData> logExperimentalData = experimentalData.stream()
                .map(experimentalData -> experimentalData.withAb2(Math.log(experimentalData.getAb2())))
                .toList();
        int pointsCnt = logExperimentalData.size();
        double ab2max = logExperimentalData.get(pointsCnt - 1).getAb2();
        List<List<ExperimentalData>> logSplitData = new ArrayList<>();
        logSplitData.add(logExperimentalData.stream()
                .filter(experimentalData -> experimentalData.getAb2() <= ab2max / 3.0)
                .collect(Collectors.toList()));
        logSplitData.add(logExperimentalData.stream()
                .filter(experimentalData -> experimentalData.getAb2() <= ab2max * 2.0 / 3.0 && experimentalData.getAb2() > ab2max / 3.0)
                .collect(Collectors.toList()));
        logSplitData.add(logExperimentalData.stream()
                .filter(experimentalData -> experimentalData.getAb2() <= ab2max && experimentalData.getAb2() > ab2max * 2.0 / 3.0)
                .collect(Collectors.toList()));

        List<ModelLayer> modelLayers = new ArrayList<>();
        for (int i = 0; i < logSplitData.size(); i++) {

            if (logSplitData.get(i).size() == 0) {
               return new ArrayList<>();
            }

            double prevLast;
            List<ExperimentalData> list = logSplitData.get(i);
            double avg = list.stream()
                    .map(ExperimentalData::getResistanceApparent)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);
            if (i > 0) {
                prevLast = Math.exp(logSplitData.get(i - 1).get(logSplitData.get(i - 1).size() - 1).getAb2());
            } else {
                prevLast = 0;
            }
            //От последнего в этом слою отнимаем последний в прошлом
            modelLayers.add(ModelLayer.create(Math.exp(list.get(list.size() - 1).getAb2()) - prevLast, avg));
        }
        modelLayers.set(modelLayers.size() - 1, modelLayers.get(modelLayers.size() - 1).withPower(0));
        return modelLayers;
    }
}
