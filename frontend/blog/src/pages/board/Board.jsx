import React from "react";
import styled from "styled-components";
import BatteryBoard from "./BatteryBoard";
import ChatComponent from "../../components/chatbot/ChatComponent";
import BMSData from "../../components/analysis/BMSdata";
import AnalysisResult from "../../components/analysis/AnalysisResult"
import { BiLineChart } from "react-icons/bi";

const Board = () => {
  return (
    <S.Container>
      <S.Title>
        <BiLineChart></BiLineChart>측정 데이터
      </S.Title>
      <BatteryBoard></BatteryBoard>
      <BMSData></BMSData>
      <AnalysisResult></AnalysisResult>
      <ChatComponent />
    </S.Container>
  );
};

const S = {
  Container: styled.div``,
  Title: styled.div`
    font-size: 30px;
    font-weight: bold;
    color: #1428a0;
  `,
};

export default Board;
