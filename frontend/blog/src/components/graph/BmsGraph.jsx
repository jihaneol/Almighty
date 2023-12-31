import React, { forwardRef } from "react";
import Highcharts from "highcharts/highstock";
import HighchartsReact from "highcharts-react-official";
import styled from "styled-components";
import BMSIcon from "../../assets/images/icon-counter.png";

const BmsGraph = ({ data }) => {
  const option = {
    chart: {
      type: "column",
      backgroundColor: "#f9f9f9",
      borderRadius: 10,
      plotShadow: true,
    },
    accessibility: {
      enabled: false,
    },

    exporting: {
      enabled: false,
    },

    title: {
      text: ``,
    },

    subtitle: {
      text: "",
      align: "left",
    },

    plotOptions: {
      column: {
        minPointLength: 3, // 최소 높이 설정
      },
      label: {
        connectorAllowed: false,
      },
      line: {
        // 선 그래프에 대한 설정
        lineWidth: 2, // 선의 굵기 설정 (기본값은 2)
      },
    },
    yAxis: {
      min: 0, // 막대 그래프의 최소 값 설정

      title: {
        text: "Count",
      },
    },

    xAxis: { type: "category" },

    legend: {
      enabled: false, // 범례 비활성화
    },
    tooltip: {
      formatter: function () {
        return "<b>" + this.key + " : " + this.y + "</b>";
      },
    },
    series: [
      {
        colors: [
          data && data["overVoltageCount"] >= 5 ? "#FF0000" : "#4589F4",
          data && data["underVoltageCount"] >= 5 ? "#FF0000" : "#4589F4",
          data && data["overCurrentCount"] >= 5 ? "#FF0000" : "#4589F4",
          data && data["overTemperatureCount"] >= 5 ? "#FF0000" : "#4589F4",
          data && data["underTemperatureCount"] >= 5 ? "#FF0000" : "#4589F4",
        ],
        colorByPoint: true,

        data: [
          ["과전압", data ? data["overVoltageCount"] : 0],
          ["저전압", data ? data["underVoltageCount"] : 0],
          ["과전류", data ? data["overCurrentCount"] : 0],
          ["고온도", data ? data["overTemperatureCount"] : 0],
          ["저온도", data ? data["underTemperatureCount"] : 0],
        ],
        dataLabels: {
          enabled: true,
          color: "#FFFFFF",
          align: "center",
          format: "{point.y:.f}", // one decimal
          y: 10, // 10 pixels down from the top
          style: {
            fontSize: "13px",
            fontFamily: "Verdana, sans-serif",
          },
        },
      },
    ],

    responsive: {
      rules: [
        {
          condition: {
            maxWidth: 500,
          },
          chartOptions: {},
        },
      ],
    },
  };
  return (
    <S.Wrap>
      <div>
        <img src={BMSIcon} className="bar"></img>
        <p>BMS</p>
      </div>
      <HighchartsReact highcharts={Highcharts} options={option} />
    </S.Wrap>
  );
};

const S = {
  Wrap: styled.div`
    padding-left: 5px;
    z-index: 1;
    div {
      display: flex;
      flex-direction: row;
      .bar {
        width: 35px;
        height: 35px;
      }
    }
    div > p {
      color: #034f9e;
      margin-bottom: 0px;
      margin-left: 10px;
      font-weight: bold;
      font-size: 20px;
    }
  `,
};
export default BmsGraph;
