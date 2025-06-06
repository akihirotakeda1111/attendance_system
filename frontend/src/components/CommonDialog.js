import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from "@mui/material";

const CommonDialog = ({ open, onClose, title, content, onConfirm, onDelete }) => {
  const handleDelete = (executeDelete) => {
    const confirmed = window.confirm("本当に削除しますか？\nデータは完全に削除されます");
    
    if (confirmed) {
      executeDelete();
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="lg">
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>{content}</DialogContent>
      <DialogActions>
        {onDelete && <Button className="danger-button" onClick={() => handleDelete(onDelete)}>Delete</Button>}
        <Button onClick={onClose}>Close</Button>
        {onConfirm && <Button onClick={onConfirm} color="primary">OK</Button>}
      </DialogActions>
    </Dialog>
  );
};

export default CommonDialog;