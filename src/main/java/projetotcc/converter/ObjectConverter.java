package projetotcc.converter;

import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import projetotcc.model.Base;

@FacesConverter("objectConverter")
public class ObjectConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		
		// Validação de possível string vazia, retornamos um objeto nulo.
		if (value.equals("")) {
			return null; 
		}
		
		// Tentamos converter o String value para um Long id
		try {
			Long id = Long.valueOf(value);
			Collection items = (Collection) component.getAttributes().get("items");			
			
			return findById(items, id);

		} catch (Exception ex) {
			throw new ConverterException("Não foi possível aplicar conversão de item com valor "
					+ "["+ value + "] no componente [" + component.getId()+ "]", ex);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {

		// Primeira linha em branco da combo caso exista,
		// Aqui simulamos que o id desse campo em branco seja "-1"
		if (value == null || value == "") {
			return "-1";
		}

		// Validando se o value é uma instancia da interface base (todas as entidades -
		// Aluno, Professor e Agendamento
		// implementam esta interface) se sim, retorno o id desse objeto, convertendo
		// para string.
		if (value instanceof Base) {
			return ((Base) value).getId().toString();
		} else {
			return "-1";
		}
	}
	
	/** Retorna o objeto pelo id  */
	private Object findById(Collection collection, Long idToFind) {

		for (Object obj : collection) {
			
			if (obj instanceof Base && idToFind.equals(((Base) obj).getId())) {
				return obj;
			}			
		}
		return null;
	}

}
