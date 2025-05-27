import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from "@mui/material";

const CommonDialog = ({ open, onClose, title, content, onConfirm }) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>{content}</DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
        {onConfirm && <Button onClick={onConfirm} color="primary">OK</Button>}
      </DialogActions>
    </Dialog>
  );
};

export default CommonDialog;