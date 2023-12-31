import styled from "styled-components";

function Pagination({ total, page, setPage }) {
  const generatePageNumbers = () => {
    const pages = [];
    const p = 5 * Math.floor((page - 1) / 5);

    if (p + 5 <= total) {
      // 전체 페이지 수가 표시할 총 페이지 수보다 작을 경우, 모든 페이지를 표시
      for (let i = p; i < p + 5; i++) {
        pages.push(i);
      }
    } else {
      for (let i = p; i < total; i++) {
        pages.push(i);
      }
    }

    return pages;
  };

  return (
    <Nav>
      <Button onClick={() => setPage(page - 1)} disabled={page === 1}>
        &lt;
      </Button>
      {generatePageNumbers().map((i) => (
        <Button
          key={i + 1}
          onClick={() => setPage(i + 1)}
          aria-current={page === i + 1 ? "page" : undefined}
        >
          {i + 1}
        </Button>
      ))}
      <Button onClick={() => setPage(page + 1)} disabled={page === total}>
        &gt;
      </Button>
    </Nav>
  );
}

const Nav = styled.nav`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  margin: 16px;
`;

const Button = styled.button`
  // border: 1px solid #dee2e6;
  // border-radius: 8px;
  border: none;
  padding: 8px;
  margin: 0;
  background: 007bff;
  color: #034F9E;
  font-size: 1.5rem;

  &:hover {
    background: #DEDEDE;
    cursor: pointer;
    transform: translateY(-2px);
  }

  &[disabled] {
    background: transparent;
    cursor: revert;
    transform: revert;
  }

  &[aria-current] {
    background: #D5DFE9;
    font-weight: bold;
    cursor: revert;
    transform: revert;
    color: #034F9E;
  }
`;

export default Pagination;
