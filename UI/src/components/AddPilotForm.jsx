import {
    Button,
    TextField,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Alert,
    CircularProgress
} from '@mui/material';
import { useState } from 'react';
import axios from 'axios';

export default function AddPilotForm({ open, onClose }) {
    const [form, setForm] = useState({
        name: '',
        age: '',
        experience: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const validateForm = () => {
        if (!form.name.trim()) return 'Имя обязательно для заполнения';
        if (form.age < 18) return 'Возраст должен быть не менее 18 лет';
        if (form.experience < 0) return 'Опыт не может быть отрицательным';
        return null;
    };

    const handleSubmit = async () => {
        try {
            setLoading(true);
            const response = await axios.post('/api/pilots', form, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 201) {
                onClose();
                window.location.reload();
            }
        } catch (error) {
            setError(error.response?.data?.message || 'Ошибка сохранения');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Добавить нового пилота</DialogTitle>

            <DialogContent>
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}

                <TextField
                    autoFocus
                    margin="dense"
                    label="Имя"
                    fullWidth
                    value={form.name}
                    onChange={e => setForm({...form, name: e.target.value})}
                    sx={{ mb: 2 }}
                />

                <TextField
                    margin="dense"
                    label="Возраст"
                    type="number"
                    fullWidth
                    value={form.age}
                    onChange={e => setForm({...form, age: e.target.value})}
                    inputProps={{ min: 18 }}
                    sx={{ mb: 2 }}
                />

                <TextField
                    margin="dense"
                    label="Опыт (лет)"
                    type="number"
                    fullWidth
                    value={form.experience}
                    onChange={e => setForm({...form, experience: e.target.value})}
                    inputProps={{ min: 0 }}
                />
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} disabled={loading}>
                    Отмена
                </Button>
                <Button
                    onClick={handleSubmit}
                    variant="contained"
                    disabled={loading}
                >
                    {loading ? <CircularProgress size={24} /> : 'Сохранить'}
                </Button>
            </DialogActions>
        </Dialog>
    );
}