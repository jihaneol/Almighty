import React, { useEffect, useState } from "react";
import styled from "styled-components";
import { BsPencilSquare } from "react-icons/bs";
import ReturnConfirmTable from "../../components/table/ReturnConfirmTable";
import AlarmTable from "../../components/alarm/AlarmTable";
import http from "../../api/http";

const ReturnConfirm = () => {
  const [data, setData] = useState([]);
  useEffect(() => {
    http
      .get(`/api/batteries/request`)
      .then(({ data }) => {
        setData(() => {
          return data["data"];
        });
      })
      .catch();
  }, []);
  return (
    <S.Container>
      <S.Title>
        <BsPencilSquare />
        반품 확인 페이지
      </S.Title>
      <S.Content>
        <S.AlarmWrapper>
          <AlarmTable />
        </S.AlarmWrapper>
        <S.ReturnWrapper>
          <ReturnConfirmTable data={data} />
        </S.ReturnWrapper>
      </S.Content>
    </S.Container>
  );
};

export default ReturnConfirm;

const S = {
  Container: styled.div`
    
    display: flex;
    flex-direction: column; // 수직 방향으로 컴포넌트 배치
  `,
  Title: styled.div`
    font-size: 30px;
    font-weight: bold;
    color: #1428a0;
  `,
  Content: styled.div`
    display: flex; // Flexbox를 활성화
    justify-content: space-between; // 컴포넌트들 사이에 간격을 줍니다.
  `,
  AlarmWrapper: styled.div`
    flex: 1; // 비율을 1로 설정 (즉, 전체 너비의 50% 차지)
    margin-right: 10px; // 오른쪽에 약간의 간격을 줍니다.
  `,
  ReturnWrapper: styled.div`
    flex: 2; // 비율을 2로 설정 (즉, 전체 너비의 50% 차지)
  `,
};
