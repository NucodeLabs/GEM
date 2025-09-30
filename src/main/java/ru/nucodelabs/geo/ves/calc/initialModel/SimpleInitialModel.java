package ru.nucodelabs.geo.ves.calc.initialModel;

import ru.nucodelabs.geo.ves.ExperimentalData;
import ru.nucodelabs.geo.ves.ModelLayer;
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.nucodelabs.geo.ves.JavaApi.copy;

public class SimpleInitialModel {
    private SimpleInitialModel() {
    }

    public static List<ModelLayer> threeLayersInitialModel(List<ReadOnlyExperimentalSignal> experimentalData) {
        if (experimentalData.size() <= 3) {
            throw new IllegalStateException("Для построения стартовой модели требуется ≥ 4 измерений, было " + experimentalData.size());
        }

        List<ExperimentalData> logExperimentalData = experimentalData.stream()
            .map(data -> new ExperimentalData(
                Math.log(data.getAb2()),
                data.getMn2(),
                data.getAmperage(),
                data.getVoltage(),
                data.getResistanceApparent(),
                data.getErrorResistanceApparent(),
                data.isHidden()
            ))
            .toList();
        double ab2min = logExperimentalData.getFirst().getAb2();
        double ab2max = logExperimentalData.getLast().getAb2();
        double ab2range = ab2max - ab2min;
        List<List<ExperimentalData>> logSplitData = new ArrayList<>();
        logSplitData.add(logExperimentalData.stream()
            .filter(data -> data.getAb2() <= ab2min + (ab2range / 3.0))
            .collect(Collectors.toList()));
        logSplitData.add(logExperimentalData.stream()
            .filter(data -> data.getAb2() <= ab2min + (ab2range * 2.0 / 3.0) && data.getAb2() > ab2min + (ab2range / 3.0))
            .collect(Collectors.toList()));
        logSplitData.add(logExperimentalData.stream()
            .filter(data -> data.getAb2() > ab2min + (ab2range * 2.0 / 3.0))
            .collect(Collectors.toList()));

        List<ModelLayer> modelLayers = new ArrayList<>();
        for (int i = 0; i < logSplitData.size(); i++) {

            if (logSplitData.get(i).isEmpty()) {
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
                prevLast = Math.exp(logSplitData.get(i - 1).getLast().getAb2());
            } else {
                prevLast = 0;
            }
            //От последнего в этом слою отнимаем последний в прошлом
            modelLayers.add(new ModelLayer(Math.exp(list.getLast().getAb2()) - prevLast, avg, false, false));
        }
        modelLayers.set(modelLayers.size() - 1, copy(modelLayers.getLast()).power(0).build());
        return modelLayers;
    }
}
