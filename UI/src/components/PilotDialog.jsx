import { useState, useEffect } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, Alert, CircularProgress
} from '@mui/material';
import axios from 'axios';

export default function PilotDialog({
                                        open,
                                        onClose,
                                        onError,
                                        onSuccess,
                                        pilotToEdit
                                    }) {
    const [form, setForm] = useState({
        name: '',
        age: '',
        experience: ''
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const isEditMode = !!pilotToEdit;

    useEffect(() => {
        if (pilotToEdit) {
            setForm(pilotToEdit);
        } else {
            setForm({ name: '', age: '', experience: '' });
        }
    }, [pilotToEdit]);

    const validate = () => {
        const newErrors = {};
        if (!form.name.trim()) newErrors.name = 'Введите имя';
        if (form.age < 18) newErrors.age = 'Минимальный возраст 18 лет';
        if (form.experience < 0) newErrors.experience = 'Опыт не может быть отрицательным';
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validate()) return;

        setLoading(true);
        try {
            const url = isEditMode
                ? `/api/pilots/${pilotToEdit.id}`
                : '/api/pilots';

            const method = isEditMode ? 'put' : 'post';

            await axios[method](url, form);

            onSuccess(isEditMode
                ? 'Пилот успешно обновлен'
                : 'Пилот успешно создан');
            onClose();
        } catch (error) {
            onError(error.response?.data?.message || 'Ошибка операции');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>
                {isEditMode ? 'Редактировать пилота' : 'Добавить нового пилота'}
            </DialogTitle>

            <DialogContent sx={{ pt: 2 }}>
                {Object.values(errors).map((error, i) => (
                    <Alert key={i} severity="error" sx={{ mb: 2 }}>{error}</Alert>
                ))}

                <TextField
                    label="Имя"
                    fullWidth
                    sx={{ mb: 2 }}
                    error={!!errors.name}
                    value={form.name}
                    onChange={e => setForm({...form, name: e.target.value})}
                />

                <TextField
                    label="Возраст"
                    type="number"
                    fullWidth
                    sx={{ mb: 2 }}
                    error={!!errors.age}
                    value={form.age}
                    onChange={e => setForm({...form, age: e.target.value})}
                    inputProps={{ min: 18 }}
                />

                <TextField
                    label="Опыт (лет)"
                    type="number"
                    fullWidth
                    error={!!errors.experience}
                    value={form.experience}
                    onChange={e => setForm({...form, experience: e.target.value})}
                    inputProps={{ min: 0 }}
                />
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} disabled={loading}>Отмена</Button>
                <Button
                    onClick={handleSubmit}
                    variant="contained"
                    disabled={loading}
                >
                    {loading ? (
                        <CircularProgress size={24} />
                    ) : isEditMode ? 'Обновить' : 'Сохранить'}
                </Button>
            </DialogActions>
        </Dialog>
    );
}