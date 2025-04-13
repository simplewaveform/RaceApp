import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from '@mui/material';

export default function ConfirmationDialog({ open, onClose, onConfirm, title, content }) {
    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>{content}</DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Отмена</Button>
                <Button onClick={onConfirm} color="error" variant="contained">Удалить</Button>
            </DialogActions>
        </Dialog>
    );
}