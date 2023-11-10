package com.batteryalmighty.bms.processing;

import com.batteryalmighty.bms.domain.mysql.Battery;
import com.batteryalmighty.bms.domain.mysql.BmsBoard;
import com.batteryalmighty.bms.domain.mongo.VitBoard;
import com.batteryalmighty.bms.domain.mysql.Model;
import com.batteryalmighty.bms.repository.mysql.BatteryRepository;
import com.batteryalmighty.bms.repository.mysql.BmsBoardRepository;
import com.batteryalmighty.bms.repository.mongo.VitBoardRepository;
import com.batteryalmighty.bms.repository.mysql.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BmsProcessing {
    private final VitBoardRepository vitBoardRepository;
    private final MongoTemplate mongoTemplate;
    private final BmsBoardRepository bmsBoardRepository;
    private final BatteryRepository batteryRepository;
    private final ModelRepository modelRepository;
    private final Ekf ekf;

    public void predict(){
        List<VitBoard> vitBoards = vitBoardRepository.findVitBoardByProgressId(6L);
        Battery battery = batteryRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("찾는 배터리가 없습니다."));
        Model model = modelRepository.findById(battery.getId())
                .orElseThrow(() -> new IllegalStateException("찾는 배터리 모델이 없습니다."));
        BmsBoard bmsBoard = bmsBoardRepository.findByProgressId(6L)
                .orElseThrow(() -> new IllegalStateException("찾는 BMS가 없습니다."));


        ekf.init();
//        double overVolt = 4.213;
//        double underVolt = 2.4707;
//        double overCurrent = 1.495;
//        double overTemperatureCharge = 50;
//        double overTemperatureDischarge = 60;
//        double underTemperatureCharge = 0;
//        double underTemperatureDischarge = -10;
        int overVoltageCount = 0;
        int underVoltageCount = 0;
        int overCurrentCount = 0;
        int underTemperatureCount = 0;
        int overTemperatureCount = 0;
        double prevCurrent = 0;
        double prevVolt = 0;
        double prevTemperature = 10;
        for (VitBoard vitBoard : vitBoards) {
            ekf.predictx_(vitBoard);
            ekf.predictP();
            ekf.kalmanGain(vitBoard.getCurrent(), vitBoard.getVoltage());
            ekf.predictx(vitBoard.getVoltage());
            ekf.nextP();

            // ekf 업데이트
            // vitBoard.predictEkf(ekf.get());

            Query query1 = new Query(Criteria.where("_id").is(vitBoard.getId()));
            Update updateEkf = new Update();
            updateEkf.set("Soc", ekf.get());
            mongoTemplate.updateFirst(query1, updateEkf, VitBoard.class);

            if(prevVolt < model.getOverVoltageThreshold() && vitBoard.getVoltage() >= model.getOverVoltageThreshold()) overVoltageCount++;
            if(prevVolt > model.getUnderVoltageThreshold() && vitBoard.getVoltage() <= model.getUnderVoltageThreshold()) underVoltageCount++;

            // 충전 상태 (양전류)
            if(vitBoard.getCurrent() > 0){
                if(prevCurrent < model.getOverCurrentChargeThreshold() && vitBoard.getCurrent() >= model.getOverCurrentChargeThreshold()) overCurrentCount++;
//                if(prevTemperature < model.getMinTemperatureChargeThreshold() && vitBoard.getTemperature() >= model.getMinTemperatureChargeThreshold()) underTemperatureCount++;
//                if(prevTemperature < model.getMaxTemperatureChargeThreshold() && vitBoard.getTemperature() >= model.getMaxTemperatureChargeThreshold()) overTemperatureCount++;
            }

            // 방전 상태 (음전류)
            if(vitBoard.getCurrent() <= 0){
                if(prevCurrent < model.getOverCurrentDischargeThreshold() && vitBoard.getCurrent() >= model.getOverCurrentDischargeThreshold()) overCurrentCount++;
//                if(prevTemperature < model.getMinTemperatureDischargeThreshold() && vitBoard.getTemperature() >= model.getMinTemperatureDischargeThreshold()) underTemperatureCount++;
//                if(prevTemperature < model.getMaxTemperatureDischargeThreshold() && vitBoard.getTemperature() >= model.getMaxTemperatureDischargeThreshold()) overTemperatureCount++;
            }

            // 전에 값 갱신
            prevVolt = vitBoard.getVoltage();
            prevCurrent = vitBoard.getCurrent();
//            prevTemperature = vitBoard.getTemperature();
        }

//        BmsBoard bmsBoard = BmsBoard.builder()
//                .overVoltageCount(overVoltageCount)
//                .underVoltageCount(underVoltageCount)
//                .overCurrentCount(overCurrentCount)
//                .underTemperatureCount(underTemperatureCount)
//                .overTemperatureCount(overTemperatureCount)
//                .build();

        bmsBoard.setBmsCount(overVoltageCount, underVoltageCount, overCurrentCount);
        bmsBoardRepository.save(bmsBoard);
    }
}
