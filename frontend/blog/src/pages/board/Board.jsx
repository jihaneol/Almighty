import React, { useState, useRef } from "react";
import styled, { createGlobalStyle } from "styled-components";
import BatteryBoard from "./BatteryBoard";
import { useNavigate } from 'react-router-dom';
import ChatComponent from "../../components/chatbot/ChatComponent";
import RegisterReason from "../../components/analysis/RegistReason";
import BMSData from "../../components/analysis/BMSdata";
import AnalysisResult from "../../components/analysis/AnalysisResult";
import RegistResult from "../../components/analysis/RegistResult";
import { BiLineChart } from "react-icons/bi";
import { useRecoilValue } from "recoil";
import { MemberIdState, AccessTokenState,RoleState,IsLoginState } from "../../states/states";
import FirebaseComponent from "../../config/firebase-messaging-sw";
import SideBar from "../../components/sidebar/Sidebar";
import RegistIcon from "../../assets/images/icon-regist.png";

const Board = () => {
  const [progress, setProgress] = useState(null);
  const [isRegistModalOpen, setIsRegistModalOpen] = useState(false);
  const modalRef = useRef < HTMLDivElement > null;
  const [progressData, setProgressData] = useState(null);
  const Role = useRecoilValue(RoleState);
  const isLogin = useRecoilValue(IsLoginState);
  const navigate = useNavigate();
  FirebaseComponent();

  const openRegistModal = () => {
    setIsRegistModalOpen(!isRegistModalOpen);
  };

  const closeRegistModal = () => {
    setIsRegistModalOpen(false);
  };

  //--------------------------------------------------------------------------------
  //이거 나중엔 풉시다 user, admin 구분 코드
  // useEffect(() => {
  //   if(isLogin != true){
  //     alert("로그인 하세요")
  //     navigate('/');
  //   }
  //   // Role이 'USER'이면 /return으로 리다이렉트
  //   if (Role === 'USER') {
  //     navigate('/return');
  //   }
  // }, [Role, navigate, isLogin]);
  //--------------------------------------------------------------------------------

  // const modalOutSideClick = (e) => {
  //   if (modalRef.current && modalRef.current.contains(e.target)) {
  //     return;
  //   }
  //   setIsRegistModalOpen(false);
  // };

  return (
    <S.Wrap>
      <SideBar progress={progress} setProgress={setProgress}></SideBar>
      <MainComp>
        <S.Summary>
          <RegisterReason
            progressData={progressData}
            setProgressData={setProgressData}
          ></RegisterReason>
          <AnalysisResult progressId={progress}></AnalysisResult>
        </S.Summary>
        <BMSData></BMSData>
        <S.Graph>
          {/* <BiLineChart></BiLineChart> */}
          <BatteryBoard
            progressId={progress}
            setProgress={setProgress}
            progressData={progressData}
            setProgressData={setProgressData}
          ></BatteryBoard>
        </S.Graph>
        <img src={RegistIcon} alt="regist" onClick={openRegistModal} />
        <RegistResult
          progress={progress}
          setProgress={setProgress}
          // modalRef={modalRef}
          // modalOutSideClick={modalOutSideClick}
          isOpen={isRegistModalOpen}
          onClose={closeRegistModal}
        ></RegistResult>
      </MainComp>
    </S.Wrap>
  );
};

const S = {
  Wrap: styled.div`
    > img {
      position: fixed;
      right: 20px;
      bottom: 20px;
      width: 70px;
      height: 70px;
      cursor: pointer;

      &:hover {
        filter: saturate(50%);
      }
    }
  `,
  Container: styled.div`
    display: flex;
    flex-direction: column;
  `,
  Graph: styled.div`
    display: flex;
    flex-direction: row;
    width: 98%;
  `,
  Summary: styled.div`
    display: flex;
    flex-direction: row;
    width: 98%;
  `,
};

const MainComp = styled.div``;

export default Board;
