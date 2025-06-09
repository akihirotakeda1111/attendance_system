import { useState } from "react";
import Message from "./Message";

const itemsPerPage = 10;

const TableWithPaging = ({ thCols, tdRows }) => {
  const [currentPage, setCurrentPage] = useState(1);

  const totalPages = tdRows.length <= 0 ? 1 : Math.ceil(tdRows.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const visibleRows = tdRows.length <= 0 ? null : tdRows.slice(startIndex, endIndex);

  const handleNext = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  const handlePrev = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };
  
  return (
    <>
      <div className="pagination-controls">
        <button onClick={handlePrev} 
          disabled={currentPage === 1}>
          前へ
        </button>
        <span className="pagination-page">{currentPage} / {totalPages}</span>
        <button onClick={handleNext} 
          disabled={currentPage === totalPages}>
          次へ
        </button>
      </div>
      <table className="table-layout">
        <thead>
          <tr>
            {thCols.map((col, index) => (
              <th key={index} className="center">{col}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {visibleRows ? visibleRows : (
            <tr><td colSpan={thCols.length}><Message type="noData" /></td></tr>
          )}
        </tbody>
      </table>
    </>
  );
};

export default TableWithPaging;