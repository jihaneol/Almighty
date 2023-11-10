import React, { forwardRef } from "react";
import Highcharts from "highcharts/highstock";
import HighchartsReact from "highcharts-react-official";
import styled from "styled-components";

const BmsGraph = ({ data, threshold, type, num }) => {
  console.log(data);
  const option = {
    chart: {
      type: "column",
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

    yAxis: {
      title: {
        text: "Count",
      },
    },

    xAxis: { type: "category" },

    legend: {
      enabled: false, // 범례 비활성화
    },

    plotOptions: {
      label: {
        connectorAllowed: false,
      },
      line: {
        // 선 그래프에 대한 설정
        lineWidth: 2, // 선의 굵기 설정 (기본값은 2)
      },
    },

    series: [
      {
        colors: [
          "#9b20d9",
          "#9215ac",
          "#861ec9",
          "#7a17e6",
          "#7010f9",
          "#691af3",
          "#6225ed",
          "#5b30e7",
          "#533be1",
          "#4c46db",
          "#4551d5",
          "#3e5ccf",
          "#3667c9",
          "#2f72c3",
          "#277dbd",
          "#1f88b7",
          "#1693b1",
          "#0a9eaa",
          "#03c69b",
          "#00f194",
        ],
        colorByPoint: true,

        data: [
          ["과전압", data["overVoltageCount"]],
          ["저전압", data["underVoltageCount"]],
          ["과전류", data["overCurrentCount"]],
          ["온도이상", data["abnormalTemperatureCount"]],
        ],
        dataLabels: {
          enabled: true,
          color: "#FFFFFF",
          align: "right",
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
      <img
        class="BMS-INFO"
        src="/Vector.png"
        alt="Grapefruit slice atop a pile of other slices"
      />
      <S.Info>잘했다.</S.Info>
      <p>BMS</p>

      <div>
        <HighchartsReact highcharts={Highcharts} options={option} />
      </div>
    </S.Wrap>
  );
};

const S = {
  Wrap: styled.div`
    flex: 1;
    padding: 10px;
    .BMS-INFO {
      position: relative;
      left: 67px;
      top: 15px;
    }
    > div {
      box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2),
        0 6px 20px 0 rgba(0, 0, 0, 0.19);
    }

    > p {
      color: #034f9e;
      margin-bottom: 0px;
      margin-left: 20px;
      font-weight: bold;
      font-size: 20px;
    }
  `,
  Info: styled.div`
    position: relative;
    left: 55px;
    top: 15px;
    width: 100px;
    height: 100px;
  `,
};
export default BmsGraph;
