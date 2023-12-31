import React, { useEffect, useRef, useState } from "react";
import MetaGraph from "./../../components/graph/MetaGraph";
import TestGraph from "./../../components/graph/TestGraph";
import http from "../../api/http";

import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import MetaGraphImpedance from "../../components/graph/MetaGraphImpedance";
import styled from "styled-components";
import { BiSolidChart } from "react-icons/bi";
import BmsGraph from "../../components/graph/BmsGraph";

const match = {
  0: "voltageMeasured",
  1: "currentMeasured",
  2: "temperatureMeasured",
};
const BatteryBoard = ({
  progressId,
  setProgress,
  progressData,
  setProgressData,
  vitData,
  bmsData,
  battery,
}) => {
  const [isopenbmsinfo, setIsopenbmsinfo] = useState(false);
  const [isopentestinfo, setIsopentestinfo] = useState(false);

  return (
    <S.Wrap>
      <div className="BMS">
        <div className="img-bms">
          <img
            src="/Vector.png"
            onMouseEnter={(e) => {
              setIsopenbmsinfo(true);
              e.cancelBubble = true;
            }}
            onMouseLeave={(e) => {
              setIsopenbmsinfo(false);
              e.cancelBubble = true;
            }}
          />
          {isopenbmsinfo && (
            <div>
              <p>
                측정 기간동안 나타난 전압, 전력, 온도 이상의 횟수를 나타내는
                그래프
              </p>
            </div>
          )}
        </div>
        <BmsGraph data={bmsData}></BmsGraph>
      </div>

      <div className="Test">
        <div className="img-test">
          <img
            src="/Vector.png"
            onMouseEnter={(e) => {
              setIsopentestinfo(true);
            }}
            onMouseLeave={(e) => {
              setIsopentestinfo(false);
            }}
          />
          {isopentestinfo && (
            <div>
              <p>
                시간에 따른 전압, 전력, 온도와 배터리 잔량을 나타내는 그래프
              </p>
            </div>
          )}
        </div>

        <TestGraph
          data={vitData}
          threshold={battery}
          type={["voltageMeasured", "currentMeasured", "temperatureMeasured"]}
        ></TestGraph>
      </div>
    </S.Wrap>
  );
};

const S = {
  Wrap: styled.div`
    margin-top: 20px;

    > div {
      margin-bottom: 70px;
    }
    .BMS {
      position: relative;
      float: right;
      width: 50%;
      height: 100%;

      .img-bms {
        z-index: 1;
        position: absolute;
        left: 105px;
        div {
          padding: 10px;
          opacity: 0.7;
          width: 300px;
          left: 30px;
          top: -10px;
          text-align: center;
          position: relative;
          border-radius: 10px;
          background-color: #d9d9d9;
        }
      }
    }
    .Test {
      position: relative;
      float: left;
      width: 50%;
      height: 100%;
      .img-test {
        z-index: 1;
        position: absolute;
        left: 95px;
        div {
          padding: 10px;
          opacity: 0.7;
          width: 300px;
          left: 30px;
          top: -10px;
          text-align: center;
          position: relative;
          border-radius: 10px;
          background-color: #d9d9d9;
        }
      }
    }

    border-radius: 10px;
    width: 100%;
  `,
  Title: styled.span`
    font-size: 20px;
    font-weight: bold;
    color: #1428a0;
  `,
  Info: styled.div`
    z-index: 98;
    position: relative;
    left: 55px;
    top: 15px;
    width: 100px;
    height: 100px;
  `,
  Info1: styled.div`
    position: relative;
    left: 55px;
    top: 15px;
    width: 100px;
    height: 100px;
    z-index: 99;
  `,
};
export default BatteryBoard;
